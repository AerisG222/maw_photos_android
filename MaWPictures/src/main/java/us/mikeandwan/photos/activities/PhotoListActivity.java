package us.mikeandwan.photos.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.data.Photo;
import us.mikeandwan.photos.data.PhotoAndCategory;
import us.mikeandwan.photos.data.PhotoDownload;
import us.mikeandwan.photos.data.PhotoSize;
import us.mikeandwan.photos.fragments.CommentDialogFragment_;
import us.mikeandwan.photos.fragments.ExifDialogFragment_;
import us.mikeandwan.photos.fragments.MainImageFragment;
import us.mikeandwan.photos.fragments.MainImageToolbarFragment;
import us.mikeandwan.photos.fragments.RatingDialogFragment_;
import us.mikeandwan.photos.fragments.ThumbnailListFragment;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.BackgroundTaskPriority;
import us.mikeandwan.photos.tasks.DownloadImageBackgroundTask;
import us.mikeandwan.photos.tasks.GetPhotoListBackgroundTask;
import us.mikeandwan.photos.tasks.GetRandomPhotoBackgroundTask;

@SuppressWarnings("ALL")
@SuppressLint("Registered")
@OptionsMenu(R.menu.photo_list)
@EActivity(R.layout.activity_photo_list)
public class PhotoListActivity extends AppCompatActivity implements IPhotoActivity {
    private static final float FADE_START_ALPHA = 1.0f;
    public static final float FADE_END_ALPHA = 0.2f;
    public static final int FADE_DURATION = 4200;

    // kept running into OOM with 5, try to decrease this to reduce consumption
    private static final int RANDOM_PREFETCH_COUNT = 10;
    private static final int PREFETCH_COUNT = 3;

    private Activity _theActivity;
    private ScheduledThreadPoolExecutor _slideshowExecutor;
    private HorizontalScrollView _thumbnailScrollView;

    @InstanceState
    protected int _index = 0;

    @InstanceState
    protected boolean _isRandomView;

    @InstanceState
    protected boolean _displayedRandomImage;

    @InstanceState
    protected boolean _playingSlideshow;

    @InstanceState
    protected ArrayList<Photo> _photoList = new ArrayList<>();

    @OptionsMenuItem(R.id.action_share)
    protected MenuItem _menuShare;

    @ViewById(R.id.progressBar)
    protected ProgressBar _progressBar;

    @ViewById(R.id.toolbar)
    protected Toolbar _toolbar;

    @FragmentById(R.id.thumbnailListFragment)
    protected ThumbnailListFragment _thumbnailListFragment;

    @FragmentById(R.id.mainImageToolbarFragment)
    protected MainImageToolbarFragment _imageToolbarFragment;

    @FragmentById(R.id.mainImageFragment)
    protected MainImageFragment _mainImageFragment;

    @ViewById(R.id.bottomLayout)
    protected LinearLayout _bottomLayout;

    @Extra("URL")
    protected String _url;

    @Extra("NAME")
    protected String _name;


    @AfterViews
    protected void afterViews() {
        _theActivity = this;

        if (_toolbar != null) {
            setSupportActionBar(_toolbar);

            getSupportActionBar().setTitle(_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ViewCompat.setElevation(_toolbar, 8);
        }

        _thumbnailScrollView = _thumbnailListFragment.getThumbnailScrollView();
        _thumbnailScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fade(_thumbnailScrollView);

                return false;
            }
        });

        fade();
    }


    @OptionsItem(android.R.id.home)
    protected void onMenuItemHome() {
        finish();
    }


    @OptionsItem(R.id.action_settings)
    protected void onMenuItemSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i(MawApplication.LOG_TAG, "onResume - PhotoListActivity");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        displayToolbar(sharedPrefs.getBoolean("display_toolbar", true));
        displayThumbnails(sharedPrefs.getBoolean("display_thumbnails", true));

        // if we are coming back from an orientation change, we might already have a valid list
        // populated.  if so, use the original list.
        if(_photoList.isEmpty()) {
            if (_url.equalsIgnoreCase("random")) {
                _isRandomView = true;
                initRandomPhotos();
            } else {
                initPhotoList(_url);
            }
        } else {
            if(_playingSlideshow) {
                startSlideshow();
                _imageToolbarFragment.setSlideshowPlaying(true);
            }

            onGatherPhotoListComplete();
        }
    }


    @Override
    public void onPause() {
        Log.i(MawApplication.LOG_TAG, "onPause - PhotoListActivity");

        // make sure we kill the slideshow thread if we are leaving the activity to avoid errors
        stopSlideshow();

        super.onPause();
    }


    public Photo getCurrentPhoto() {
        return _photoList.get(_index);
    }


    public List<Photo> getPhotoList() {
        return _photoList;
    }


    public int getCurrentIndex() {
        return _index;
    }


    public boolean hasNext() {
        return _index < _photoList.size() - 1;
    }


    public boolean hasPrevious() {
        return _index > 0;
    }


    public void gotoPhoto(int index) {
        _index = index;
        displayMainImage(_photoList.get(_index));

        // try to leave a buffer of 5 images from where the user is to the end of the list
        if (_isRandomView) {
            int minEndingIndex = index + RANDOM_PREFETCH_COUNT;

            if (minEndingIndex > _photoList.size()) {
                for (int i = _photoList.size(); i < minEndingIndex; i++) {
                    fetchRandom();
                }
            }
        }
    }


    private void fade() {
        fade(_toolbar);
        fade(_imageToolbarFragment.getView());
        fade(_thumbnailScrollView);
    }


    private void fade(View view) {
        AlphaAnimation alpha = new AlphaAnimation(FADE_START_ALPHA, FADE_END_ALPHA);
        alpha.setDuration(FADE_DURATION);
        alpha.setFillAfter(true);

        view.startAnimation(alpha);
    }


    public void updateProgress() {
        Log.d(MawApplication.LOG_TAG, "task count: " + BackgroundTaskExecutor.getInstance().getTaskCount());

        if (BackgroundTaskExecutor.getInstance().getTaskCount() > 0) {
            _progressBar.setVisibility(View.VISIBLE);
        } else {
            _progressBar.setVisibility(View.INVISIBLE);
        }
    }


    public void showRating() {
        showDialog(new RatingDialogFragment_());
    }


    public void showExif() {
        showDialog(new ExifDialogFragment_());
    }


    public void showComments() {
        showDialog(new CommentDialogFragment_());
    }


    public void rotatePhoto(int direction) {
        _mainImageFragment.rotatePhoto(direction);
    }


    public void toggleSlideshow() {
        if (_slideshowExecutor != null) {
            stopSlideshow();
            _playingSlideshow = false;  // user toggled
        } else {
            startSlideshow();
            _playingSlideshow = true;  // user toggled
        }
    }


    private void initPhotoList(String url) {
        GetPhotoListBackgroundTask task = new GetPhotoListBackgroundTask(getBaseContext(), url) {
            @Override
            protected void postExecuteTask(List<Photo> result) {
                _index = 0;
                _photoList.addAll(result);

                onGatherPhotoListComplete();
            }

            @Override
            protected void handleException(ExecutionException ex) {
                if (ex.getCause() instanceof MawAuthenticationException) {
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                }
            }
        };

        BackgroundTaskExecutor.getInstance().enqueueTask(task);
        updateProgress();
    }


    private void initRandomPhotos() {
        // start with 10 images, we will dynamically increase the number of photos as the user thumbs
        // through the list
        for (int i = 0; i < RANDOM_PREFETCH_COUNT; i++) {
            fetchRandom();
        }
    }


    private void fetchRandom() {
        GetRandomPhotoBackgroundTask task = new GetRandomPhotoBackgroundTask(getBaseContext()) {
            @Override
            protected void postExecuteTask(PhotoAndCategory result) {
                _index = 0;
                _photoList.add(result.getPhoto());

                onRandomPhotoFetched();
            }

            @Override
            protected void handleException(ExecutionException ex) {
                if (ex.getCause() instanceof MawAuthenticationException) {
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                }
            }
        };

        BackgroundTaskExecutor.getInstance().enqueueTask(task);
        updateProgress();
    }


    private void onRandomPhotoFetched() {
        _mainImageFragment.onPhotoListUpdated();
        _thumbnailListFragment.onPhotoListUpdated();

        if (!_displayedRandomImage) {
            _displayedRandomImage = true;
            gotoPhoto(0);
        }

        updateProgress();
    }


    private void onGatherPhotoListComplete() {
        _mainImageFragment.onPhotoListUpdated();
        _thumbnailListFragment.onPhotoListUpdated();

        gotoPhoto(_index);

        updateProgress();
    }


    private void startSlideshow() {
        if (_slideshowExecutor == null) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int intervalSeconds = Integer.parseInt(sharedPrefs.getString("slideshow_interval", "3"));

            _slideshowExecutor = new ScheduledThreadPoolExecutor(1);

            _slideshowExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    int nextIndex = _index + 1;

                    if (nextIndex < _photoList.size()) {
                        SlideshowRunnable slideshowRunnable = new SlideshowRunnable((nextIndex));
                        _theActivity.runOnUiThread(slideshowRunnable);
                    } else {
                        _theActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleSlideshow();
                            }
                        });
                    }
                }
            }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        }
    }


    private void stopSlideshow() {
        if (_slideshowExecutor != null) {
            _slideshowExecutor.shutdownNow();
            _slideshowExecutor = null;

            _imageToolbarFragment.setSlideshowPlaying(false);
        }
    }


    private Intent createShareIntent(Photo photo) {
        if (photo != null) {
            PhotoStorage ps = new PhotoStorage(this);
            Uri contentUri = ps.getSharingContentUri(photo.getMdInfo().getPath());

            if (contentUri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setDataAndType(contentUri, "image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

                return shareIntent;
            }
        }

        return null;
    }


    private void displayMainImage(Photo photo) {
        _mainImageFragment.onCurrentPhotoUpdated();
        _thumbnailListFragment.onCurrentPhotoUpdated();

        ShareActionProvider sap = (ShareActionProvider) MenuItemCompat.getActionProvider(_menuShare);

        if(sap != null) {
            sap.setShareIntent(createShareIntent(photo));
        }

        prefetchAdjacentImages(_index);

        updateProgress();
    }


    private void prefetchAdjacentImages(int index) {
        // start fetching next item first, if there is one, as it is more likely to move forward first
        for (int i = index + 1; i < index + PREFETCH_COUNT && i < _photoList.size(); i++) {
            PhotoDownload pd = new PhotoDownload(_photoList.get(i), i);
            downloadImage(pd, PhotoSize.Md, BackgroundTaskPriority.Low);
        }

        for (int i = index - 1; i > index - PREFETCH_COUNT && i > 0; i--) {
            PhotoDownload pd = new PhotoDownload(_photoList.get(i), i);
            downloadImage(pd, PhotoSize.Md, BackgroundTaskPriority.Low);
        }
    }


    private void downloadImage(final PhotoDownload photoDownload, PhotoSize size, BackgroundTaskPriority priority) {
        DownloadImageBackgroundTask task = new DownloadImageBackgroundTask(getBaseContext(), photoDownload, size, priority) {
            @Override
            protected void postExecuteTask(PhotoDownload result) {
                updateProgress();
            }

            @Override
            protected void handleException(ExecutionException ex) {
                if (ex.getCause() instanceof MawAuthenticationException) {
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                }
            }
        };

        BackgroundTaskExecutor.getInstance().enqueueTask(task);
        updateProgress();
    }


    private void showDialog(DialogFragment fragment) {
        ensureSlideshowStopped();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        fragment.show(ft, "dialog");
    }


    private void ensureSlideshowStopped() {
        if (_slideshowExecutor != null) {
            toggleSlideshow();
        }
    }


    private void displayThumbnails(boolean doShow) {
        showFragment(_thumbnailListFragment, doShow);
    }


    private void displayToolbar(boolean doShow) {
        showFragment(_imageToolbarFragment, doShow);
    }


    private void showFragment(Fragment f, boolean doShow) {
        FragmentManager fm = getFragmentManager();

        if (doShow) {
            fm.beginTransaction()
                .show(f)
                .commit();
        } else {
            fm.beginTransaction()
                .hide(f)
                .commit();
        }
    }


    class SlideshowRunnable implements Runnable {
        private final int _nextIndex;

        public SlideshowRunnable(int nextIndex) {
            _nextIndex = nextIndex;
        }


        @Override
        public void run() {
            gotoPhoto(_nextIndex);
        }
    }
}
