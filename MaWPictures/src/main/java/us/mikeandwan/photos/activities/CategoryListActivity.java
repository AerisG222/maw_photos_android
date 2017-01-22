package us.mikeandwan.photos.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.fragments.BaseCategoryListFragment;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.tasks.GetRecentCategoriesTask;


public class CategoryListActivity extends AppCompatActivity implements ICategoryListActivity {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private int _year;
    private List<Category> _categories;
    private BaseCategoryListFragment _activeFragment;
    private BaseCategoryListFragment _categoryListFragment;
    private BaseCategoryListFragment _categoryThumbnailsFragment;
    private MenuItem _refreshMenuItem;

    @BindView(R.id.toolbar) Toolbar _toolbar;

    @Inject MawDataManager _dataManager;
    @Inject GetRecentCategoriesTask _getRecentCategoriesTask;


    protected void afterBind() {
        if (_toolbar != null) {
            setSupportActionBar(_toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(String.valueOf(_year));

            ViewCompat.setElevation(_toolbar, 8);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);

        _year = getIntent().getIntExtra("YEAR", 0);
        _categoryListFragment = (BaseCategoryListFragment) getFragmentManager().findFragmentById(R.id.category_list_fragment);
        _categoryThumbnailsFragment = (BaseCategoryListFragment) getFragmentManager().findFragmentById(R.id.category_thumbnails_fragment);

        afterBind();
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        FragmentManager fm = getFragmentManager();
        BaseCategoryListFragment fragmentToHide;

        if (sharedPrefs.getBoolean("category_thumbnail_view", true)) {
            _activeFragment = _categoryListFragment;
            fragmentToHide = _categoryThumbnailsFragment;
        } else {
            _activeFragment = _categoryThumbnailsFragment;
            fragmentToHide = _categoryListFragment;
        }

        fm.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .show(_activeFragment)
            .hide(fragmentToHide)
            .commit();

        _categories = _dataManager.getCategoriesForYear(_year);

        _activeFragment.setCategories(_categories);

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // do not send event after activity has been destroyed
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
                        .observeOn(Schedulers.single())
                        .subscribe(
                                x -> onSyncComplete(x),
                                ex -> onException(ex)
                        )
        );

        ImageView iv = (ImageView) getLayoutInflater().inflate(R.layout.refresh_indicator, _toolbar, false);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        _refreshMenuItem.setActionView(iv);
    }


    private void onException(Throwable ex) {
        _refreshMenuItem.getActionView().clearAnimation();
        _refreshMenuItem.setActionView(null);

        if (ex.getCause() instanceof MawAuthenticationException) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private void onSyncComplete(List<Category> result) {
        // force the update to categories to come from database, and not the network result, as there
        // may have been updates already pulled from the poller
        _categories.clear();
        _categories.addAll(_dataManager.getCategoriesForYear(_year));

        _activeFragment.notifyCategoriesUpdated();

        _refreshMenuItem.getActionView().clearAnimation();
        _refreshMenuItem.setActionView(null);
    }
}
