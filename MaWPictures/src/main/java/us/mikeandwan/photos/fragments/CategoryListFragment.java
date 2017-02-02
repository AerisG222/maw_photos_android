package us.mikeandwan.photos.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.ui.CategoryRecyclerAdapter;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;


public class CategoryListFragment extends BaseCategoryListFragment {
    private Unbinder _unbinder;

    @BindView(R.id.category_recycler_view) RecyclerView _categoryRecyclerView;

    @Inject Activity _activity;
    @Inject PhotoStorage _photoStorage;
    @Inject DownloadCategoryTeaserTask _downloadCategoryTeaserTask;
    @Inject CategoryRecyclerAdapter _adapter;


    @Override
    public void setCategories(List<Category> categories) {
        super.setCategories(categories);

        _adapter.setCategoryList(categories);
        _adapter.getClicks().subscribe(c -> getCategoryActivity().selectCategory(c));

        _categoryRecyclerView.setAdapter(_adapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        _categoryRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(_activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _categoryRecyclerView.setLayoutManager(llm);

        DividerItemDecoration dec = new DividerItemDecoration(_categoryRecyclerView.getContext(), llm.getOrientation());
        _categoryRecyclerView.addItemDecoration(dec);

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
