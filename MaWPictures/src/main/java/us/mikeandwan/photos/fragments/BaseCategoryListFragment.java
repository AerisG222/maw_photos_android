package us.mikeandwan.photos.fragments;

import java.util.List;

import us.mikeandwan.photos.activities.ICategoryListActivity;
import us.mikeandwan.photos.models.Category;


public class BaseCategoryListFragment extends BaseFragment {
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
