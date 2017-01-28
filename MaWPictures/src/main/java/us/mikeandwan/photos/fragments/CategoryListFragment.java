package us.mikeandwan.photos.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.ui.CategoryRecyclerAdapter;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;


public class CategoryListFragment extends BaseCategoryListFragment {
    private CategoryRecyclerAdapter _adapter;
    private Unbinder _unbinder;

    @BindView(R.id.category_recycler_view) RecyclerView _categoryRecyclerView;

    @Inject Activity _activity;
    @Inject PhotoStorage _photoStorage;
    @Inject DownloadCategoryTeaserTask _downloadCategoryTeaserTask;


    @Override
    public void setCategories(List<Category> categories) {
        super.setCategories(categories);

        _adapter = new CategoryRecyclerAdapter(_activity, _photoStorage, _downloadCategoryTeaserTask, categories);
        _categoryRecyclerView.setAdapter(_adapter);
    }


    //@OnItemSelected(R.id.category_recycler_view)
    void onCategoryListItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getItemAtPosition((position));

        getCategoryActivity().selectCategory(category);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        //_categoryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(_activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _categoryRecyclerView.setLayoutManager(llm);

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


    public void notifyCategoriesUpdated() {
        super.notifyCategoriesUpdated();

        _adapter.notifyDataSetChanged();
    }
}
