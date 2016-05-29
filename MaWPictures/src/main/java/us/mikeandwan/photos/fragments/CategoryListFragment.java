package us.mikeandwan.photos.fragments;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
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


@EFragment(R.layout.fragment_category_list)
public class CategoryListFragment extends BaseCategoryListFragment {
    private CategoryArrayAdapter _adapter;
    private PhotoStorage _photoStorage;

    @ViewById(R.id.category_list_view)
    protected ListView categoryListView;

    @App
    protected MawApplication _app;


    @AfterInject
    protected void afterInject() {
        _photoStorage = new PhotoStorage(_app);
    }


    @Override
    public void setCategories(List<Category> categories) {
        super.setCategories(categories);

        _adapter = new CategoryArrayAdapter(this.getActivity(), _categories);

        categoryListView.setAdapter(_adapter);
    }


    @ItemClick(R.id.category_list_view)
    void onCategoryListItemClick(Category category) {
        getCategoryActivity().selectCategory(category);
    }


    public void notifyCategoriesUpdated() {
        super.notifyCategoriesUpdated();

        _adapter.notifyDataSetChanged();
    }

    private void displayCategory(CategoryRowDetail detail) {
        String file = "file://" + _photoStorage.getCachePath(detail.getCategory().getTeaserPhotoInfo().getPath());

        Picasso
            .with(getActivity())
            .load(file)
            .resizeDimen(R.dimen.category_list_thumbnail_size, R.dimen.category_list_thumbnail_size)
            .centerCrop()
            .into(detail.getImageView());
    }


    public class CategoryArrayAdapter extends ArrayAdapter<Category> {
        private final Context _context;
        private final List<Category> _categories;

        public CategoryArrayAdapter(Context context, List<Category> categories) {
            super(context, R.layout.category_list_item, categories);
            _context = context;
            _categories = categories;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_list_item, parent, false);
            }

            Category category = _categories.get(position);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnailImageView);
            TextView textView = (TextView) convertView.findViewById(R.id.categoryNameTextView);

            textView.setText(category.getName());
            imageView.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

            CategoryRowDetail row = new CategoryRowDetail(imageView, textView, category);

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

            return convertView;
        }
    }
}
