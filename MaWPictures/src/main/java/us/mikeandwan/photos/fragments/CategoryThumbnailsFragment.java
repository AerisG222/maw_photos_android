package us.mikeandwan.photos.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.concurrent.ExecutionException;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity_;
import us.mikeandwan.photos.data.Category;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.BackgroundTask;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserBackgroundTask;
import us.mikeandwan.photos.widget.CategoryRowDetail;


@SuppressWarnings("ALL")
@EFragment(R.layout.fragment_category_thumbnails)
public class CategoryThumbnailsFragment extends BaseCategoryListFragment {
    @Bean
    PhotoStorage _photoStorage;

    @ViewById(R.id.gridview)
    GridView _gridView;

    @App
    MawApplication _app;

    @RootContext
    Context _context;


    @Override
    public void setCategories(List<Category> categories) {
        super.setCategories(categories);

        CategoryThumbnailArrayAdapter adapter = new CategoryThumbnailArrayAdapter(_context, _categories);

        _gridView.setAdapter(adapter);
    }


    @ItemClick(R.id.gridview)
    void onCategoryListItemClick(Category category) {
        getCategoryActivity().selectCategory(category);
    }


    private void displayCategory(CategoryRowDetail detail) {
        String file = "file://" + _photoStorage.getCachePath(detail.getCategory().getTeaserPhotoInfo().getPath());

        Picasso
            .with(getActivity())
            .load(file)
            .resize(_gridView.getColumnWidth(), _gridView.getColumnWidth())
            .centerCrop()
            .into(detail.getImageView());
    }


    public class CategoryThumbnailArrayAdapter extends ArrayAdapter<Category> {
        private final Context _context;
        private final List<Category> _categories;

        public CategoryThumbnailArrayAdapter(Context context, List<Category> categories) {
            super(context, R.layout.category_list_item, categories);
            _context = context;
            _categories = categories;
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
            CategoryRowDetail row = new CategoryRowDetail(imageView, category);

            imageView.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

            if (_photoStorage.doesExist(category.getTeaserPhotoInfo().getPath())) {
                displayCategory(row);
            } else {
                BackgroundTask task = new DownloadCategoryTeaserBackgroundTask(getActivity(), row) {
                    @Override
                    protected void postExecuteTask(CategoryRowDetail rowDetail) {
                        displayCategory(rowDetail);
                    }

                    @Override
                    protected void handleException(ExecutionException ex) {
                        if (ex.getCause() instanceof MawAuthenticationException) {
                            startActivity(new Intent(getActivity(), LoginActivity_.class));
                        }
                    }
                };

                BackgroundTaskExecutor.getInstance().enqueueTask(task);
            }

            return imageView;
        }
    }
}
