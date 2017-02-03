package us.mikeandwan.photos.models.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;


public class CategoryThumbnailArrayAdapter extends ArrayAdapter<Category> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final Context _context;
    private final PhotoStorage _photoStorage;
    private final DownloadCategoryTeaserTask _downloadCategoryTeaserTask;
    private final AuthenticationExceptionHandler _authHandler;


    public CategoryThumbnailArrayAdapter(Context context, PhotoStorage photoStorage, DownloadCategoryTeaserTask downloadTeaserTask, AuthenticationExceptionHandler authHandler) {
        super(context, R.layout.category_list_item, new ArrayList<>());

        _context = context;
        _photoStorage = photoStorage;
        _downloadCategoryTeaserTask = downloadTeaserTask;
        _authHandler = authHandler;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(parent.getContext());
            imageView.setMinimumHeight(R.dimen.category_grid_thumbnail_size);
            imageView.setMinimumWidth(R.dimen.category_grid_thumbnail_size);
        } else {
            imageView = (ImageView) convertView;
        }

        Category category = this.getItem(position);

        if (_photoStorage.doesExist(category.getTeaserPhotoInfo().getPath())) {
            displayCategory(category, imageView);
        } else {
            imageView.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

            _disposables.add(Flowable.fromCallable(() -> _downloadCategoryTeaserTask.call(category))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            x -> displayCategory(category, imageView),
                            _authHandler::handleException
                    )
            );
        }

        return imageView;
    }


    public void setCategories(List<Category> categories) {
        this.clear();
        this.addAll(categories);
    }


    private void displayCategory(Category category, ImageView imageView) {
        String file = "file://" + _photoStorage.getCachePath(category.getTeaserPhotoInfo().getPath());

        Picasso
                .with(_context)
                .load(file)
                .resize(R.dimen.category_grid_thumbnail_size, R.dimen.category_grid_thumbnail_size)
                .centerCrop()
                .into(imageView);
    }


    public void dispose() {
        _disposables.dispose();
    }
}
