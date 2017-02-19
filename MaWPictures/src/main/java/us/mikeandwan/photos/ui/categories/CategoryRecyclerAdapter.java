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
import us.mikeandwan.photos.services.DataServices;


public abstract class CategoryRecyclerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    protected final Context _context;
    private final DataServices _dataServices;
    private final PublishSubject<Category> _categorySubject = PublishSubject.create();
    private final AuthenticationExceptionHandler _authHandler;
    private List<Category> _categoryList;


    public CategoryRecyclerAdapter(Context context,
                                   DataServices dataServices,
                                   AuthenticationExceptionHandler authHandler) {
        _context = context;
        _dataServices = dataServices;
        _authHandler = authHandler;
    }


    @Override
    public abstract T onCreateViewHolder(ViewGroup parent, int viewType);


    protected abstract void displayCategory(Category category, String imageFile, T viewHolder);


    @Override
    public void onBindViewHolder(T viewHolder, int position) {
        final Category category = _categoryList.get(position);

        viewHolder.itemView.setOnClickListener(v -> _categorySubject.onNext(category));

        _disposables.add(Flowable.fromCallable(() -> _dataServices.downloadCategoryTeaser(category))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> displayCategory(category, x, viewHolder),
                        _authHandler::handleException
                )
        );
    }


    @Override
    public int getItemCount() {
        return _categoryList.size();
    }


    void setCategoryList(List<Category> categoryList) {
        _categoryList = categoryList;
    }


    Observable<Category> onCategorySelected(){
        return _categorySubject.hide();
    }


    void dispose() {
        _disposables.dispose();
    }
}
