package us.mikeandwan.photos.ui.categories;

import java.util.List;

import us.mikeandwan.photos.ui.BaseFragment;
import us.mikeandwan.photos.models.Category;


public class BaseCategoryListFragment extends BaseFragment {
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
