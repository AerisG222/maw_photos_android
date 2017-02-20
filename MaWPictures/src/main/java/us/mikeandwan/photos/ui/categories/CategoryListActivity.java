package us.mikeandwan.photos.ui.categories;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.prefs.CategoryDisplay;
import us.mikeandwan.photos.prefs.CategoryDisplayPreference;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.services.PhotoListType;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.photos.PhotoListActivity;
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
    @Inject AuthenticationExceptionHandler _authHandler;
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

            _gridAdapter.onCategorySelected().subscribe(this::selectCategory);

            if(_decoration != null) {
                _categoryRecyclerView.removeItemDecoration(_decoration);
                _decoration = null;
            }

            _categoryRecyclerView.setAdapter(_gridAdapter);
        }
        else {
            _listAdapter.setCategoryList(categories);

            _listAdapter.onCategorySelected().subscribe(this::selectCategory);

            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
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

        if(!_authHandler.handleException(ex)) {
            _refreshMenuItem.getActionView().clearAnimation();
            _refreshMenuItem.setActionView(null);
        }
    }


    private void notifyCategoriesUpdated() {
        if (_categoryPrefs.getCategoryDisplay() == CategoryDisplay.ThumbnailGrid) {
            _gridAdapter.notifyDataSetChanged();
        }
        else {
            _listAdapter.notifyDataSetChanged();
        }
    }


    private void onSyncComplete(List<Category> result) {
        // force the update to categories to come from database, and not the network result, as there
        // may have been updates already pulled from the poller
        _categories.clear();
        _categories.addAll(_dataServices.getCategoriesForYear(_year));

        notifyCategoriesUpdated();

        stopSyncAnimation();
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
