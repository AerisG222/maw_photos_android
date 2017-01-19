package us.mikeandwan.photos.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserBackgroundTask;
import us.mikeandwan.photos.models.ui.CategoryRowDetail;


public class CategoryListFragment extends BaseCategoryListFragment {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private CategoryArrayAdapter _adapter;
    private Unbinder _unbinder;

    @BindView(R.id.category_list_view) ListView categoryListView;

    @Bean
    PhotoStorage _photoStorage;

    @Bean
    DownloadCategoryTeaserBackgroundTask _downloadCategoryTeaserTask;


    @Override
    public void setCategories(List<Category> categories) {
        super.setCategories(categories);

        _adapter = new CategoryArrayAdapter(getActivity(), _categories);

        categoryListView.setAdapter(_adapter);
    }


    @OnItemSelected(R.id.category_list_view)
    void onCategoryListItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getItemAtPosition((position));

        getCategoryActivity().selectCategory(category);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear(); // do not send event after activity has been destroyed
        _unbinder.unbind();
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


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }


    public class CategoryArrayAdapter extends ArrayAdapter<Category> {
        private final List<Category> _categories;

        public CategoryArrayAdapter(Context context, List<Category> categories) {
            super(context, R.layout.category_list_item, categories);
            _categories = categories;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.category_list_item, parent, false);
            }

            Category category = _categories.get(position);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnailImageView);
            TextView textView = (TextView) convertView.findViewById(R.id.categoryNameTextView);

            textView.setText(category.getName());
            imageView.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

            CategoryRowDetail row = new CategoryRowDetail(imageView, category);

            if (_photoStorage.doesExist(category.getTeaserPhotoInfo().getPath())) {
                displayCategory(row);
            } else {
                disposables.add(Flowable.fromCallable(() -> _downloadCategoryTeaserTask.call(row))
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.single())
                                .subscribe(
                                        x -> displayCategory(x),
                                        ex -> handleException(ex)
                                )
                );
            }

            return convertView;
        }
    }
}
