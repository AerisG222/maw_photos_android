package us.mikeandwan.photos.activities;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.concurrent.ExecutionException;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.data.Category;
import us.mikeandwan.photos.data.MawDataManager;
import us.mikeandwan.photos.fragments.BaseCategoryListFragment;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.tasks.BackgroundTask;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.GetRecentCategoriesBackgroundTask;

@SuppressWarnings("ALL")
@SuppressLint("Registered")
@EActivity(R.layout.activity_category_list)
@OptionsMenu(R.menu.category_list)
public class CategoryListActivity extends AppCompatActivity implements ICategoryListActivity {
    private MawDataManager _dm;
    private List<Category> _categories;
    private BaseCategoryListFragment _activeFragment;

    @OptionsMenuItem(R.id.action_force_sync)
    protected MenuItem _refreshMenuItem;

    @ViewById(R.id.toolbar)
    protected Toolbar _toolbar;

    @Extra("YEAR")
    protected int _year;

    @FragmentById(R.id.category_list_fragment)
    protected BaseCategoryListFragment _categoryListFragment;

    @FragmentById(R.id.category_thumbnails_fragment)
    protected BaseCategoryListFragment _categoryThumbnailsFragment;

    @App
    MawApplication _app;


    @AfterInject
    protected void afterInject() {
        _dm = new MawDataManager(_app);
    }


    @AfterViews
    protected void afterViews() {
        if (_toolbar != null) {
            setSupportActionBar(_toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(String.valueOf(_year));

            ViewCompat.setElevation(_toolbar, 8);
        }
    }


    @Override
    public void onResume() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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

        _categories = _dm.getCategoriesForYear(_year);

        _activeFragment.setCategories(_categories);

        super.onResume();
    }


    @OptionsItem(android.R.id.home)
    protected void onMenuHomeClick() {
        finish();
    }


    @OptionsItem(R.id.action_settings)
    protected void onMenuSettingsClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @OptionsItem(R.id.action_force_sync)
    protected void onMenuSyncClick() {
        forceSync();
    }


    public void selectCategory(Category category) {
        Intent intent = new Intent(getBaseContext(), PhotoListActivity_.class);
        intent.putExtra("NAME", category.getName());
        intent.putExtra("URL", PhotoApiClient.getPhotosForCategoryUrl(category.getId()));

        startActivity(intent);
    }


    private void forceSync() {
        BackgroundTask task = new GetRecentCategoriesBackgroundTask(getBaseContext()) {
            @Override
            protected void postExecuteTask(List<Category> result) {
                onSyncComplete();
            }

            @Override
            protected void handleException(ExecutionException ex) {
                _refreshMenuItem.getActionView().clearAnimation();
                _refreshMenuItem.setActionView(null);

                if (ex.getCause() instanceof MawAuthenticationException) {
                    startActivity(new Intent(getBaseContext(), LoginActivity_.class));
                }
            }
        };

        BackgroundTaskExecutor.getInstance().enqueueTask(task);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_indicator, _toolbar, false);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        _refreshMenuItem.setActionView(iv);
    }


    private void onSyncComplete() {
        // force the update to categories to come from database, and not the network result, as there
        // may have been updates already pulled from the poller
        _categories.clear();
        _categories.addAll(_dm.getCategoriesForYear(_year));

        _activeFragment.notifyCategoriesUpdated();

        _refreshMenuItem.getActionView().clearAnimation();
        _refreshMenuItem.setActionView(null);
    }
}
