package us.mikeandwan.photos.models.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;


// TODO: butterknife?
public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final Context _context;
    private final PhotoStorage _photoStorage;
    private final DownloadCategoryTeaserTask _downloadCategoryTeaserTask;
    private final PublishSubject<Category> onClickSubject = PublishSubject.create();
    private final AuthenticationExceptionHandler _authHandler;
    private List<Category> _categoryList;


    public CategoryRecyclerAdapter(Context context, PhotoStorage photoStorage, DownloadCategoryTeaserTask downloadTeaserTask, AuthenticationExceptionHandler authHandler) {
        _context = context;
        _photoStorage = photoStorage;
        _downloadCategoryTeaserTask = downloadTeaserTask;
        _authHandler = authHandler;
    }


    @Override
    public CategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View categoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);

        return new ViewHolder(categoryView);
    }


    @Override
    public void onBindViewHolder(CategoryRecyclerAdapter.ViewHolder viewHolder, int position) {
        final Category category = _categoryList.get(position);

        viewHolder.itemView.setOnClickListener(v -> onClickSubject.onNext(category));

        viewHolder._nameTextView.setText(category.getName());

        if (_photoStorage.doesExist(category.getTeaserPhotoInfo().getPath())) {
            displayCategory(category, viewHolder);
        } else {
            viewHolder._thumbnailImageView.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

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


    public void setCategoryList(List<Category> categoryList) {
        _categoryList = categoryList;
    }


    public Observable<Category> getClicks(){
        return onClickSubject.hide();
    }


    public void dispose() {
        _disposables.dispose();
    }


    private void displayCategory(Category category, CategoryRecyclerAdapter.ViewHolder viewHolder) {
        String file = "file://" + _photoStorage.getCachePath(category.getTeaserPhotoInfo().getPath());

        viewHolder._nameTextView.setText(category.getName());

        Picasso
                .with(_context)
                .load(file)
                .resizeDimen(R.dimen.category_list_thumbnail_size, R.dimen.category_list_thumbnail_size)
                .centerCrop()
                .into(viewHolder._thumbnailImageView);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnailImageView) ImageView _thumbnailImageView;
        @BindView(R.id.categoryNameTextView) TextView _nameTextView;


        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
