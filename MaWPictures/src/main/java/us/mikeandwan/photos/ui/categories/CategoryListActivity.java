package us.mikeandwan.photos.ui.categories;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.ui.photos.PhotoListActivity;
import us.mikeandwan.photos.ui.settings.SettingsActivity;
import us.mikeandwan.photos.di.DaggerTaskComponent;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.tasks.GetRecentCategoriesTask;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;


public class CategoryListActivity extends BaseActivity implements ICategoryListActivity, HasComponent<TaskComponent> {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private int _year;
    private List<Category> _categories;
    private MenuItem _refreshMenuItem;
    private TaskComponent _taskComponent;
    private CategoryListFragment _listFragment;

    @BindView(R.id.toolbar) Toolbar _toolbar;

    @Inject MawDataManager _dataManager;
    @Inject GetRecentCategoriesTask _getRecentCategoriesTask;
    @Inject AuthenticationExceptionHandler _authHandler;
    @Inject SharedPreferences _sharedPrefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);

        _taskComponent = DaggerTaskComponent.builder()
                .applicationComponent(getApplicationComponent())
                .taskModule(getTaskModule())
                .build();

        _taskComponent.inject(this);

        _year = getIntent().getIntExtra("YEAR", 0);
        _listFragment = (CategoryListFragment) getFragmentManager().findFragmentById(R.id.category_list_fragment);

        updateToolbar(_toolbar, String.valueOf(_year));
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
        _categories = _dataManager.getCategoriesForYear(_year);

        _listFragment.setCategories(_categories);

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // do not send event after activity has been destroyed
    }


    public TaskComponent getComponent() {
        return _taskComponent;
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
        intent.putExtra("URL", PhotoApiClient.getPhotosForCategoryUrl(category.getId()));

        startActivity(intent);
    }


    private void forceSync() {
        disposables.add(
                Flowable.fromCallable(() -> _getRecentCategoriesTask.call())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onSyncComplete,
                                this::onException
                        )
        );

        ImageView iv = (ImageView) getLayoutInflater().inflate(R.layout.refresh_indicator, _toolbar, false);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        _refreshMenuItem.setActionView(iv);
    }


    private void onException(Throwable ex) {
        if(!_authHandler.handleException(ex)) {
            _refreshMenuItem.getActionView().clearAnimation();
            _refreshMenuItem.setActionView(null);
        }
    }


    private void onSyncComplete(List<Category> result) {
        // force the update to categories to come from database, and not the network result, as there
        // may have been updates already pulled from the poller
        _categories.clear();
        _categories.addAll(_dataManager.getCategoriesForYear(_year));

        _listFragment.notifyCategoriesUpdated();

        _refreshMenuItem.getActionView().clearAnimation();
        _refreshMenuItem.setActionView(null);
    }
}
