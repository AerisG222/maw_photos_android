package us.mikeandwan.photos.models.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;


// TODO: butterknife?  dagger?
public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final List<Category> _categoryList;
    private final Context _context;
    private final PhotoStorage _photoStorage;
    private final DownloadCategoryTeaserTask _downloadCategoryTeaserTask;


    public CategoryRecyclerAdapter(Context context, PhotoStorage photoStorage, DownloadCategoryTeaserTask downloadTeaserTask, List<Category> categoryList) {
        _categoryList = categoryList;
        _context = context;
        _photoStorage = photoStorage;
        _downloadCategoryTeaserTask = downloadTeaserTask;
    }


    @Override
    public CategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.category_list_item, parent, false);

        return new ViewHolder(contactView);
    }


    @Override
    public void onBindViewHolder(CategoryRecyclerAdapter.ViewHolder viewHolder, int position) {
        Category category = _categoryList.get(position);

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
                            ex -> handleException(ex)
                    )
            );
        }
    }


    @Override
    public int getItemCount() {
        return _categoryList.size();
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


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            _context.startActivity(new Intent(_context, LoginActivity.class));
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView _thumbnailImageView;
        public TextView _nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            _nameTextView = (TextView) itemView.findViewById(R.id.categoryNameTextView);
            _thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnailImageView);
        }
    }
}
