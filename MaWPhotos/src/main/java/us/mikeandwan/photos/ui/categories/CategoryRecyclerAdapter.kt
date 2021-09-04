package us.mikeandwan.photos.ui.categories;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.DataServices;


public abstract class CategoryRecyclerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    protected final Context _context;
    protected final DataServices _dataServices;
    private final PublishSubject<Category> _categorySubject = PublishSubject.create();
    private List<Category> _categoryList;
    ICategoryListActivity _activity;


    public CategoryRecyclerAdapter(ICategoryListActivity activity,
                                   DataServices dataServices) {
        _context = (Context) activity;
        _activity = activity;
        _dataServices = dataServices;
    }


    @NonNull
    @Override
    public abstract T onCreateViewHolder(@NonNull ViewGroup parent, int viewType);


    protected abstract void displayCategory(Category category, String imageFile, T viewHolder);


    protected abstract String downloadCategoryTeaser(Category category);


    @Override
    public void onBindViewHolder(@NonNull T viewHolder, int position) {
        final Category category = _categoryList.get(position);

        viewHolder.itemView.setOnClickListener(v -> _categorySubject.onNext(category));

        _disposables.add(Flowable.fromCallable(() -> downloadCategoryTeaser(category))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> displayCategory(category, x, viewHolder),
                        ex -> _activity.onApiException(ex)
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
