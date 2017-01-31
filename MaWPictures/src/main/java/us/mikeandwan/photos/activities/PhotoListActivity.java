package us.mikeandwan.photos.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.DaggerTaskComponent;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.fragments.CommentDialogFragment;
import us.mikeandwan.photos.fragments.ExifDialogFragment;
import us.mikeandwan.photos.fragments.MainImageFragment;
import us.mikeandwan.photos.fragments.MainImageToolbarFragment;
import us.mikeandwan.photos.fragments.RatingDialogFragment;
import us.mikeandwan.photos.fragments.ThumbnailListFragment;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;
import us.mikeandwan.photos.tasks.GetPhotoListTask;
import us.mikeandwan.photos.tasks.GetRandomPhotoTask;


public class PhotoListActivity extends BaseActivity implements IPhotoActivity, HasComponent<TaskComponent> {
    private static final float FADE_START_ALPHA = 1.0f;
    public static final float FADE_END_ALPHA = 0.2f;
    public static final int FADE_DURATION = 4200;

    private static final int RANDOM_INITIAL_COUNT = 20;
    private static final int PREFETCH_COUNT = 2;

    private static final String STATE_INDEX = "index";
    private static final String STATE_IS_RANDOM_VIEW = "is_random_view";
    private static final String STATE_DISPLAY_RANDOM_IMAGE = "display_random_image";
    private static final String STATE_PLAYING_SLIDESHOW = "playing_slideshow";
    private static final String STATE_PHOTO_LIST = "photo_list";

    private final CompositeDisposable disposables = new CompositeDisposable();
    private Activity _theActivity;
    private ScheduledThreadPoolExecutor _slideshowExecutor;
    private HorizontalScrollView _thumbnailScrollView;
    private HashSet<Integer> _randomPhotoIds;
    private int _taskCount = 0;
    private String _url;
    private String _name;
    private ThumbnailListFragment _thumbnailListFragment;
    private MainImageToolbarFragment _imageToolbarFragment;
    private MainImageFragment _mainImageFragment;
    private MenuItem _menuShare;
    private TaskComponent _taskComponent;

    @BindView(R.id.progressBar) ProgressBar _progressBar;
    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.bottomLayout) LinearLayout _bottomLayout;

    @Inject PhotoStorage _ps;
    @Inject GetPhotoListTask _getPhotoListTask;
    @Inject GetRandomPhotoTask _getRandomPhotoTask;
    @Inject DownloadPhotoTask _downloadPhotoTask;

    private int _index = 0;
    private boolean _isRandomView;
    private boolean _displayedRandomImage;
    private boolean _playingSlideshow;
    private ArrayList<Photo> _photoList = new ArrayList<>();


    public TaskComponent getComponent() {
        return _taskComponent;
    }


    protected void afterBind() {
        _theActivity = this;

        if (_toolbar != null) {
            setSupportActionBar(_toolbar);

            getSupportActionBar().setTitle(_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ViewCompat.setElevation(_toolbar, 8);
        }

        /*
        _thumbnailScrollView = _thumbnailListFragment.getThumbnailScrollView();
        _thumbnailScrollView.setOnTouchListener((view, event) -> {
                fade(_thumbnailScrollView);
                return false;
        });
        */

        fade();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        ButterKnife.bind(this);

        _taskComponent = DaggerTaskComponent.builder()
                .applicationComponent(getApplicationComponent())
                .taskModule(getTaskModule())
                .build();

        _taskComponent.inject(this);

        _thumbnailListFragment = (ThumbnailListFragment) getFragmentManager().findFragmentById(R.id.thumbnailListFragment);
        _imageToolbarFragment = (MainImageToolbarFragment) getFragmentManager().findFragmentById(R.id.mainImageToolbarFragment);
        _mainImageFragment = (MainImageFragment) getFragmentManager().findFragmentById(R.id.mainImageFragment);

        _url = getIntent().getStringExtra("URL");
        _name = getIntent().getStringExtra("NAME");

        if(savedInstanceState != null) {
            _index = savedInstanceState.getInt(STATE_INDEX);
            _isRandomView = savedInstanceState.getBoolean(STATE_IS_RANDOM_VIEW);
            _displayedRandomImage = savedInstanceState.getBoolean(STATE_DISPLAY_RANDOM_IMAGE);
            _playingSlideshow = savedInstanceState.getBoolean(STATE_PLAYING_SLIDESHOW);
            _photoList = (ArrayList<Photo>) savedInstanceState.getSerializable(STATE_PHOTO_LIST);
        }

        afterBind();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_list, menu);

        _menuShare = menu.findItem(R.id.action_share);

        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_INDEX, _index);
        outState.putBoolean(STATE_IS_RANDOM_VIEW, _isRandomView);
        outState.putBoolean(STATE_DISPLAY_RANDOM_IMAGE, _displayedRandomImage);
        outState.putBoolean(STATE_PLAYING_SLIDESHOW, _playingSlideshow);
        outState.putSerializable(STATE_PHOTO_LIST, _photoList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // do not send event after activity has been destroyed
    }


    //public void onMenuItemHome(MenuItem menuItem) {
    //    finish();
    //}


    public void onMenuItemSettings(MenuItem menuItem) {
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
            int minEndingIndex = index + RANDOM_INITIAL_COUNT;

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
        /*
        AlphaAnimation alpha = new AlphaAnimation(FADE_START_ALPHA, FADE_END_ALPHA);
        alpha.setDuration(FADE_DURATION);
        alpha.setFillAfter(true);

        view.startAnimation(alpha);
        */
    }


    public void updateProgress() {
        Log.d(MawApplication.LOG_TAG, "task count: " + _taskCount);

        if (_taskCount > 0) {
            _progressBar.setVisibility(View.VISIBLE);
        } else {
            _progressBar.setVisibility(View.INVISIBLE);
        }
    }


    public void showRating() {
        showDialog(new RatingDialogFragment());
    }


    public void showExif() {
        showDialog(new ExifDialogFragment());
    }


    public void showComments() {
        showDialog(new CommentDialogFragment());
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
        disposables.add(Flowable.fromCallable(() -> _getPhotoListTask.call(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> onGetPhotoList(x),
                        ex -> handleException(ex)
                )
        );

        updateProgress();
    }



    private void onGetPhotoList(List<Photo> list) {
        _index = 0;
        _photoList.addAll(list);

        _thumbnailListFragment.addPhotoList(list);

        onGatherPhotoListComplete();
    }


    private void initRandomPhotos() {
        _randomPhotoIds = new HashSet<>();

        for (int i = 0; i < RANDOM_INITIAL_COUNT; i++) {
            fetchRandom();
        }
    }


    private void fetchRandom() {
        disposables.add(Flowable.fromCallable(() -> _getRandomPhotoTask.call())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> onGetRandom(x),
                        ex -> handleException(ex)
                )
        );

        updateProgress();
    }


    private void onGetRandom(PhotoAndCategory result)
    {
        if(_randomPhotoIds.contains(result.getPhoto().getId())) {
            // avoid duplicates
            return;
        }

        _randomPhotoIds.add(result.getPhoto().getId());

        _index = 0;
        _photoList.add(result.getPhoto());
        _thumbnailListFragment.addPhoto((result.getPhoto()));

        Log.d(MawApplication.LOG_TAG, "random photo: " + result.getPhoto().getId());

        onRandomPhotoFetched();
    }


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private void onRandomPhotoFetched() {
        _mainImageFragment.onPhotoListUpdated();

        if (!_displayedRandomImage) {
            _displayedRandomImage = true;
            gotoPhoto(0);
        }

        updateProgress();
    }


    private void onGatherPhotoListComplete() {
        _mainImageFragment.onPhotoListUpdated();

        gotoPhoto(_index);

        updateProgress();
    }


    private void startSlideshow() {
        if (_slideshowExecutor == null) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int intervalSeconds = Integer.parseInt(sharedPrefs.getString("slideshow_interval", "3"));

            _slideshowExecutor = new ScheduledThreadPoolExecutor(1);
            _slideshowExecutor.scheduleWithFixedDelay(() -> incrementSlideshow(), intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        }
    }


    private void incrementSlideshow() {
        int nextIndex = _index + 1;

        if (nextIndex < _photoList.size()) {
            SlideshowRunnable slideshowRunnable = new SlideshowRunnable((nextIndex));
            _theActivity.runOnUiThread(slideshowRunnable);
        } else {
            _theActivity.runOnUiThread(() -> toggleSlideshow());
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
            Uri contentUri = _ps.getSharingContentUri(photo.getMdInfo().getPath());

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

        PrefetchMainImage(_index);

        updateProgress();
    }


    private void PrefetchMainImage(int index) {
        // start fetching next item first, if there is one, as it is more likely to move forward first
        for (int i = index + 1; i < index + PREFETCH_COUNT && i < _photoList.size(); i++) {
            downloadImage(_photoList.get(i), PhotoSize.Md);
        }

        for (int i = index - 1; i > index - PREFETCH_COUNT && i > 0; i--) {
            downloadImage(_photoList.get(i), PhotoSize.Md);
        }
    }


    private void downloadImage(final Photo photo, PhotoSize size) {
        disposables.add(Flowable.fromCallable(() -> _downloadPhotoTask.call(photo, size))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> updateProgress(),
                        ex -> handleException(ex)
                )
        );

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
