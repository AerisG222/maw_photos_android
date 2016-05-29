package us.mikeandwan.photos.fragments;


import android.app.Fragment;

import java.util.List;

import us.mikeandwan.photos.activities.ICategoryListActivity;
import us.mikeandwan.photos.data.Category;

public class BaseCategoryListFragment extends Fragment {
    protected List<Category> _categories;

    protected ICategoryListActivity getCategoryActivity() {
        return (ICategoryListActivity) getActivity();
    }

    public void setCategories(List<Category> categories) {
        _categories = categories;
    }

    public void notifyCategoriesUpdated() {

    }
}
