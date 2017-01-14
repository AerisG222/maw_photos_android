package us.mikeandwan.photos.fragments;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

import java.util.List;
import java.util.concurrent.ExecutionException;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity_;
import us.mikeandwan.photos.data.Category;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.BackgroundTask;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserBackgroundTask;
import us.mikeandwan.photos.widget.CategoryRowDetail;
import us.mikeandwan.photos.widget.RecyclerViewAdapterBase;
import us.mikeandwan.photos.widget.ViewWrapper;


@SuppressWarnings("ALL")
@EFragment(R.layout.fragment_category_list)
public class CategoryListFragment extends BaseCategoryListFragment {
    //private CategoryArrayAdapter _adapter;
    private RecyclerView.Adapter _adapter;
    private RecyclerView.LayoutManager _layoutManager;

    @ViewById(R.id.category_recycler_view)
    protected RecyclerView categoryRecyclerView;

    @App
    protected MawApplication _app;

    @Bean
    PhotoStorage _photoStorage;

    @RootContext
    Context _context;

    @AfterInject
    protected void afterInject() {
        categoryRecyclerView.setHasFixedSize(true);

        _layoutManager = new LinearLayoutManager(_context);
        categoryRecyclerView.setLayoutManager(_layoutManager);
    }


    @Override
    public void setCategories(List<Category> categories) {
        super.setCategories(categories);

        _adapter = new CategoryArrayAdapter(_categories);

        categoryRecyclerView.setAdapter(_adapter);
    }


    @ItemClick(R.id.category_recycler_view)
    void onCategoryListItemClick(Category category) {
        getCategoryActivity().selectCategory(category);
    }


    public void notifyCategoriesUpdated() {
        super.notifyCategoriesUpdated();

        _adapter.notifyDataSetChanged();
    }




    @EBean
    public class CategoryArrayAdapter extends RecyclerViewAdapterBase<Category, CategoryRowDetail> {
        @RootContext
        Context context;

        @Override
        protected PersonItemView onCreateItemView(ViewGroup parent, int viewType) {
            return PersonItemView_.build(context);
        }

        @Override
        public void onBindViewHolder(ViewWrapper<PersonItemView> viewHolder, int position) {
            PersonItemView view = viewHolder.getView();
            Person person = items.get(position);

            view.bind(person);
        }

        public CategoryArrayAdapter(List<Category> categories) {
            super();
            this.items = categories;
        }


        @Override
        public void onBindViewHolder(ViewWrapper<CategoryRowDetail> holder, int position) {
            Category category = _categories.get(position);
            CategoryRowDetail detail = holder.getView();

            detail._textView.setText(category.getName());
            detail._imageView.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

            CategoryRowDetail row = new CategoryRowDetail(holder._imageView, category);

            if (_photoStorage.doesExist(category.getTeaserPhotoInfo().getPath())) {
                displayCategory(row);
            } else {
                BackgroundTask task = new DownloadCategoryTeaserBackgroundTask(getActivity(), row) {
                    @Override
                    protected void postExecuteTask(CategoryRowDetail rowDetail) {
                        displayCategory(rowDetail);
                    }

                    @Override
                    protected void handleException(ExecutionException ex) {
                        if (ex.getCause() instanceof MawAuthenticationException) {
                            startActivity(new Intent(getActivity(), LoginActivity_.class));
                        }
                    }
                };

                BackgroundTaskExecutor.getInstance().enqueueTask(task);
            }
        }

        @Override
        public int getItemCount() {
            return _categories.size();
        }
    }
}
