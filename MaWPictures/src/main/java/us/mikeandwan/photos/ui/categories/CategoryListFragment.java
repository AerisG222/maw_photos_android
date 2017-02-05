package us.mikeandwan.photos.ui.categories;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.ui.BaseFragment;


// TODO: further genericize the adapters
public class CategoryListFragment extends BaseFragment {
    private int _thumbSize;
    private Unbinder _unbinder;
    private ViewTreeObserver _viewTreeObserver;
    private int _lastWidth = -1;

    @BindView(R.id.category_recycler_view) RecyclerView _categoryRecyclerView;

    @Inject Activity _activity;
    @Inject SharedPreferences _sharedPrefs;
    @Inject ListCategoryRecyclerAdapter _listAdapter;
    @Inject ThumbnailCategoryRecyclerAdapter _gridAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        _thumbSize = (int)getResources().getDimension(R.dimen.category_grid_thumbnail_size);

        _categoryRecyclerView.setHasFixedSize(true);

        // http://stackoverflow.com/questions/25396747/how-to-get-fragment-width
        _viewTreeObserver = view.getViewTreeObserver();
        _viewTreeObserver.addOnGlobalLayoutListener(() -> {
            _categoryRecyclerView.post(() -> {
                if(getView() == null || getView().getWidth() == _lastWidth) {
                    return;
                }

                int width = getView().getWidth();

                if(width == _lastWidth) {
                    return;
                }

                _lastWidth = width;

                if (_sharedPrefs.getBoolean("category_thumbnail_view", true)) {
                    int cols = Math.max(1, (width / _thumbSize));
                    GridLayoutManager glm = new GridLayoutManager(_activity, cols);
                    _categoryRecyclerView.setLayoutManager(glm);
                }
            });
        });

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

        if(_listAdapter != null) {
            _listAdapter.dispose();
        }

        if(_gridAdapter != null) {
            _gridAdapter.dispose();
        }

        _unbinder.unbind();
    }


    public void setCategories(List<Category> categories) {
        if (_sharedPrefs.getBoolean("category_thumbnail_view", true)) {
            _gridAdapter.setCategoryList(categories);

            _gridAdapter.getClicks().subscribe(c -> getCategoryActivity().selectCategory(c));

            _categoryRecyclerView.setAdapter(_gridAdapter);
        }
        else {
            _listAdapter.setCategoryList(categories);

            _listAdapter.getClicks().subscribe(c -> getCategoryActivity().selectCategory(c));

            LinearLayoutManager llm = new LinearLayoutManager(_activity);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            _categoryRecyclerView.setLayoutManager(llm);

            DividerItemDecoration dec = new DividerItemDecoration(_categoryRecyclerView.getContext(), llm.getOrientation());
            _categoryRecyclerView.addItemDecoration(dec);

            _categoryRecyclerView.setAdapter(_listAdapter);
        }
    }


    public void notifyCategoriesUpdated() {
        if (_sharedPrefs.getBoolean("category_thumbnail_view", true)) {
            _gridAdapter.notifyDataSetChanged();
        }
        else {
            _listAdapter.notifyDataSetChanged();
        }
    }


    private ICategoryListActivity getCategoryActivity() {
        return (ICategoryListActivity) getActivity();
    }
}
