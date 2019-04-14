package us.mikeandwan.photos.ui.categories;

import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.di.DaggerActivityComponent;
import us.mikeandwan.photos.models.ApiCollection;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.prefs.CategoryDisplay;
import us.mikeandwan.photos.prefs.CategoryDisplayPreference;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.services.PhotoListType;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.photos.PhotoListActivity;
import us.mikeandwan.photos.ui.receiver.PhotoReceiverActivity;
import us.mikeandwan.photos.ui.settings.SettingsActivity;


public class CategoryListActivity extends BaseActivity implements ICategoryListActivity, HasComponent<ActivityComponent> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private int _year;
    private List<Category> _categories;
    private MenuItem _refreshMenuItem;
    private ActivityComponent _activityComponent;
    private ViewTreeObserver.OnGlobalLayoutListener _listener;
    private DividerItemDecoration _decoration;

    @BindDimen(R.dimen.category_grid_thumbnail_size) int _thumbSize;
    @BindView(R.id.container) ConstraintLayout _container;
    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.category_recycler_view) RecyclerView _categoryRecyclerView;

    @Inject DataServices _dataServices;
    @Inject CategoryDisplayPreference _categoryPrefs;
    @Inject ListCategoryRecyclerAdapter _listAdapter;
    @Inject ThumbnailCategoryRecyclerAdapter _gridAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);

        _activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        _activityComponent.inject(this);

        _year = getIntent().getIntExtra("YEAR", 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_list, menu);

        _refreshMenuItem = menu.findItem(R.id.action_force_sync);

        return true;
    }


    @Override
    public void onResume() {
        updateToolbar(_toolbar, String.valueOf(_year));

        _categoryRecyclerView.setHasFixedSize(true);

        if(_categoryPrefs.getCategoryDisplay() == CategoryDisplay.ThumbnailGrid) {
            // http://stackoverflow.com/questions/25396747/how-to-get-fragment-width
            _listener = () -> _categoryRecyclerView.post(() -> {
                int width = _container.getWidth();

                if (width > 0) {
                    _container.getViewTreeObserver().removeOnGlobalLayoutListener(_listener);
                    updateGridSize(width);
                }
            });

            _container.getViewTreeObserver().addOnGlobalLayoutListener(_listener);
        }

        setCategories(_dataServices.getCategoriesForYear(_year));

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        _disposables.clear(); // do not send event after activity has been destroyed
        _gridAdapter.dispose();
        _listAdapter.dispose();

        super.onDestroy();
    }


    public void onApiException(Throwable throwable) {
        handleApiException(throwable);
    }


    public ActivityComponent getComponent() {
        return _activityComponent;
    }


    public void onMenuSettingsClick(MenuItem menuItem) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public void onMenuSyncClick(MenuItem menuItem) {
        forceSync();
    }


    public void onViewUploadQueueClick(MenuItem menuItem) {
        Intent intent = new Intent(this, PhotoReceiverActivity.class);
        startActivity(intent);
    }

    public void selectCategory(Category category) {
        Intent intent = new Intent(this, PhotoListActivity.class);
        intent.putExtra("NAME", category.getName());
        intent.putExtra("TYPE", PhotoListType.ByCategory.toString());
        intent.putExtra("CATEGORY_ID", category.getId());

        startActivity(intent);
    }


    private void updateGridSize(int width) {
        int cols = Math.max(1, (width / _thumbSize));

        GridLayoutManager glm = new GridLayoutManager(this, cols);
        _categoryRecyclerView.setLayoutManager(glm);

        _categoryRecyclerView.getRecycledViewPool().clear();
    }


    private void setCategories(List<Category> categories) {
        _categories = categories;

        if (_categoryPrefs.getCategoryDisplay() == CategoryDisplay.ThumbnailGrid) {
            _gridAdapter.setCategoryList(categories);

            _disposables.add(_gridAdapter.onCategorySelected().subscribe(this::selectCategory));

            if(_decoration != null) {
                _categoryRecyclerView.removeItemDecoration(_decoration);
                _decoration = null;
            }

            _categoryRecyclerView.setAdapter(_gridAdapter);
        }
        else {
            _listAdapter.setCategoryList(categories);

            _disposables.add(_listAdapter.onCategorySelected().subscribe(this::selectCategory));

            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(RecyclerView.VERTICAL);
            _categoryRecyclerView.setLayoutManager(llm);

            if(_decoration == null) {
                _decoration = new DividerItemDecoration(_categoryRecyclerView.getContext(), llm.getOrientation());
                _categoryRecyclerView.addItemDecoration(_decoration);
            }

            _categoryRecyclerView.setAdapter(_listAdapter);
        }
    }


    private void forceSync() {
        startSyncAnimation();

        _disposables.add(
                Flowable.fromCallable(() -> _dataServices.getRecentCategories())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onSyncComplete,
                                this::onException
                        )
        );
    }


    private void onException(Throwable ex) {
        stopSyncAnimation();

        handleApiException(ex);
    }


    private void notifyCategoriesUpdated() {
        if (_categoryPrefs.getCategoryDisplay() == CategoryDisplay.ThumbnailGrid) {
            _gridAdapter.notifyDataSetChanged();
        }
        else {
            _listAdapter.notifyDataSetChanged();
        }
    }


    private void onSyncComplete(ApiCollection<Category> result) {
        stopSyncAnimation();

        long count = result.getCount();

        if(count == 0) {
            Snackbar.make(_container, "No updates available.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // TODO: use streams if/when we update api
        /*
        result
            .stream()
            .filter(x -> x.getYear() == _year)
            .collect(Collectors.toList());
        */

        List<Category> newForYear = new ArrayList<>();

        for(Category cat : result.getItems()) {
            if(cat.getYear() == _year) {
                newForYear.add(cat);
            }
        }

        String cat = "category";

        if(count != 1) {
            cat = "categories";
        }

        Snackbar.make(_container, count + " new " + cat + " found. " + newForYear.size() + " added for " + _year + ".", Snackbar.LENGTH_SHORT).show();

        if(newForYear.size() > 0) {
            _categories.addAll(newForYear);

            notifyCategoriesUpdated();
        }
    }


    private void startSyncAnimation() {
        ImageView iv = (ImageView) getLayoutInflater().inflate(R.layout.refresh_indicator, _toolbar, false);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        _refreshMenuItem.setActionView(iv);
    }


    private void stopSyncAnimation() {
        View v = _refreshMenuItem.getActionView();

        if(v != null) {
            v.clearAnimation();
            _refreshMenuItem.setActionView(null);
        }
    }
}
