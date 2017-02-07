package us.mikeandwan.photos.ui.categories;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;


public abstract class CategoryRecyclerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    protected final Context _context;
    protected final PhotoStorage _photoStorage;
    private final DownloadCategoryTeaserTask _downloadCategoryTeaserTask;
    private final PublishSubject<Category> categorySubject = PublishSubject.create();
    private final AuthenticationExceptionHandler _authHandler;
    private List<Category> _categoryList;


    public CategoryRecyclerAdapter(Context context,
                                   PhotoStorage photoStorage,
                                   DownloadCategoryTeaserTask downloadTeaserTask,
                                   AuthenticationExceptionHandler authHandler) {
        _context = context;
        _photoStorage = photoStorage;
        _downloadCategoryTeaserTask = downloadTeaserTask;
        _authHandler = authHandler;
    }


    @Override
    public abstract T onCreateViewHolder(ViewGroup parent, int viewType);


    protected abstract void displayCategory(Category category, T viewHolder);


    @Override
    public void onBindViewHolder(T viewHolder, int position) {
        final Category category = _categoryList.get(position);

        viewHolder.itemView.setOnClickListener(v -> categorySubject.onNext(category));

        if (_photoStorage.doesExist(category.getTeaserPhotoInfo().getPath())) {
            displayCategory(category, viewHolder);
        } else {
            _disposables.add(Flowable.fromCallable(() -> _downloadCategoryTeaserTask.call(category))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            x -> displayCategory(category, viewHolder),
                            _authHandler::handleException
                    )
            );
        }
    }


    @Override
    public int getItemCount() {
        return _categoryList.size();
    }


    void setCategoryList(List<Category> categoryList) {
        _categoryList = categoryList;
    }


    Observable<Category> onCategorySelected(){
        return categorySubject.hide();
    }


    void dispose() {
        _disposables.dispose();
    }
}
