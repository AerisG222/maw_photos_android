package us.mikeandwan.photos.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;


public class CategoryThumbnailsFragment extends BaseCategoryListFragment {
    private Unbinder _unbinder;

    @BindView(R.id.gridview) GridView _gridView;

    @Inject PhotoStorage _photoStorage;
    @Inject DownloadCategoryTeaserTask _downloadCategoryTeaserTask;
    @Inject CategoryThumbnailArrayAdapter _adapter;


    @Override
    public void setCategories(List<Category> categories) {
        super.setCategories(categories);

        _adapter.setCategories(categories);

        _gridView.setAdapter(_adapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_thumbnails, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getComponent(TaskComponent.class).inject(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(_adapter != null) {
            _adapter.dispose();
        }

        _unbinder.unbind();
    }


    @OnItemClick(R.id.gridview)
    void onCategoryListItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getItemAtPosition(position);

        getCategoryActivity().selectCategory(category);
    }
}
