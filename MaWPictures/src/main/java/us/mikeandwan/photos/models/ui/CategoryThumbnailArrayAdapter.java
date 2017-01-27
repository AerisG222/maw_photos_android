package us.mikeandwan.photos.models.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

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


public class CategoryThumbnailArrayAdapter extends ArrayAdapter<Category> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final Context _context;
    private final List<Category> _categories;
    private final PhotoStorage _photoStorage;
    private final DownloadCategoryTeaserTask _downloadCategoryTeaserTask;
    private final int _columnWidth;


    public CategoryThumbnailArrayAdapter(Context context, PhotoStorage photoStorage, DownloadCategoryTeaserTask downloadTeaserTask, List<Category> categories, int columnWidth) {
        super(context, R.layout.category_list_item, categories);
        _context = context;
        _categories = categories;
        _photoStorage = photoStorage;
        _downloadCategoryTeaserTask = downloadTeaserTask;
        _columnWidth = columnWidth;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(_context);
        } else {
            imageView = (ImageView) convertView;
        }

        Category category = _categories.get(position);

        if (_photoStorage.doesExist(category.getTeaserPhotoInfo().getPath())) {
            displayCategory(category, imageView);
        } else {
            imageView.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

            _disposables.add(Flowable.fromCallable(() -> _downloadCategoryTeaserTask.call(category))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            x -> displayCategory(category, imageView),
                            ex -> handleException(ex)
                    )
            );
        }

        return imageView;
    }


    private void displayCategory(Category category, ImageView imageView) {
        String file = "file://" + _photoStorage.getCachePath(category.getTeaserPhotoInfo().getPath());

        Picasso
                .with(_context)
                .load(file)
                .resize(_columnWidth, _columnWidth)
                .centerCrop()
                .into(imageView);
    }


    public void dispose() {
        _disposables.dispose();
    }


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            _context.startActivity(new Intent(_context, LoginActivity.class));
        }
    }
}
