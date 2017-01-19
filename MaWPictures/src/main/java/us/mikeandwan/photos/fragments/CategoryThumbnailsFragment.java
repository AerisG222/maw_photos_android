package us.mikeandwan.photos.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
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


public class CategoryThumbnailsFragment extends BaseCategoryListFragment {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Unbinder _unbinder;

    @BindView(R.id.gridview) GridView _gridView;

    @Bean
    PhotoStorage _photoStorage;

    @Bean
    DownloadCategoryTeaserBackgroundTask _downloadCategoryTeaserTask;


    @Override
    public void setCategories(List<Category> categories) {
        super.setCategories(categories);

        CategoryThumbnailArrayAdapter adapter = new CategoryThumbnailArrayAdapter(getActivity(), _categories);

        _gridView.setAdapter(adapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_thumbnails, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear(); // do not send event after activity has been destroyed
        _unbinder.unbind();
    }


    @OnItemClick(R.id.gridview)
    void onCategoryListItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getItemAtPosition(position);

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


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
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
                disposables.add(Flowable.fromCallable(() -> _downloadCategoryTeaserTask.call(row))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.single())
                        .subscribe(
                                x -> displayCategory(x),
                                ex -> handleException(ex)
                        )
                );
            }

            return imageView;
        }
    }
}
