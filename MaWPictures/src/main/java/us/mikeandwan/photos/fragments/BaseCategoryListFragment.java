package us.mikeandwan.photos.fragments;


import android.app.Fragment;

import java.util.List;

import us.mikeandwan.photos.activities.ICategoryListActivity;
import us.mikeandwan.photos.data.Category;

public class BaseCategoryListFragment extends Fragment {
    List<Category> _categories;

    ICategoryListActivity getCategoryActivity() {
        return (ICategoryListActivity) getActivity();
    }

    public void setCategories(List<Category> categories) {
        _categories = categories;
    }

    public void notifyCategoriesUpdated() {

    }
}
