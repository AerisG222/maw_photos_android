package us.mikeandwan.photos.ui.photos;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
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
import us.mikeandwan.photos.ui.settings.SettingsActivity;
import us.mikeandwan.photos.di.DaggerTaskComponent;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;
import us.mikeandwan.photos.tasks.GetPhotoListTask;
import us.mikeandwan.photos.tasks.GetRandomPhotoTask;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;


public class PhotoListActivity extends BaseActivity implements IPhotoActivity, HasComponent<TaskComponent> {
    private static final float FADE_START_ALPHA = 1.0f;
    public static final float FADE_END_ALPHA = 0.2f;
    public static final int FADE_DURATION = 3000;

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
    private HashSet<Integer> _randomPhotoIds;
    private int _taskCount = 0;
    private String _url;
    private MenuItem _menuShare;
    private TaskComponent _taskComponent;

    @BindView(R.id.progressBar) ProgressBar _progressBar;
    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.photoToolbar) PhotoToolbar _photoToolbar;
    @BindView(R.id.photoPager) PhotoViewPager _photoPager;
    @BindView(R.id.thumbnailPhotoRecycler) RecyclerView _thumbnailRecyclerView;

    @Inject SharedPreferences _sharedPrefs;
    @Inject PhotoStorage _ps;
    @Inject AuthenticationExceptionHandler _authHandler;
    @Inject GetPhotoListTask _getPhotoListTask;
    @Inject GetRandomPhotoTask _getRandomPhotoTask;
    @Inject DownloadPhotoTask _downloadPhotoTask;
    @Inject FullScreenImageAdapter _photoPagerAdapter;
    @Inject ThumbnailRecyclerAdapter _thumbnailRecyclerAdapter;

    private int _index = 0;
    private boolean _isRandomView;
    private boolean _displayedRandomImage;
    private boolean _playingSlideshow;
    private ArrayList<Photo> _photoList = new ArrayList<>();


    public TaskComponent getComponent() {
        return _taskComponent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            _index = savedInstanceState.getInt(STATE_INDEX);
            _isRandomView = savedInstanceState.getBoolean(STATE_IS_RANDOM_VIEW);
            _displayedRandomImage = savedInstanceState.getBoolean(STATE_DISPLAY_RANDOM_IMAGE);
            _playingSlideshow = savedInstanceState.getBoolean(STATE_PLAYING_SLIDESHOW);
            _photoList = (ArrayList<Photo>) savedInstanceState.getSerializable(STATE_PHOTO_LIST);
        }

        setContentView(R.layout.activity_photo_list);
        ButterKnife.bind(this);

        _theActivity = this;

        _taskComponent = DaggerTaskComponent.builder()
                .applicationComponent(getApplicationComponent())
                .taskModule(getTaskModule())
                .build();

        _taskComponent.inject(this);

        _thumbnailRecyclerView.setOnTouchListener((view, event) -> {
            fade(_thumbnailRecyclerView);
            return false;
        });

        _photoPager.setAdapter(_photoPagerAdapter);
        _photoPager.onPhotoSelected().subscribe(this::gotoPhoto);

        _photoToolbar.onCommentClicked().subscribe(x -> showComments());
        _photoToolbar.onExifClicked().subscribe(x -> showExif());
        _photoToolbar.onRatingClicked().subscribe(x -> showRating());
        _photoToolbar.onRotateClicked().subscribe(this::rotatePhoto);
        _photoToolbar.onToggleSlideshow().subscribe(x -> toggleSlideshow());

        _url = getIntent().getStringExtra("URL");
        String _name = getIntent().getStringExtra("NAME");

        updateToolbar(_toolbar, String.valueOf(_name));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        _thumbnailRecyclerView.setLayoutManager(llm);

        _thumbnailRecyclerAdapter.setPhotoList(getPhotoList());
        _thumbnailRecyclerView.setAdapter(_thumbnailRecyclerAdapter);
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
        _photoToolbar.dispose();
        _thumbnailRecyclerAdapter.dispose();
    }


    public void onMenuItemSettings(MenuItem menuItem) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i(MawApplication.LOG_TAG, "onResume - PhotoListActivity");

        displayToolbar(_sharedPrefs.getBoolean("display_toolbar", true));
        displayThumbnails(_sharedPrefs.getBoolean("display_thumbnails", true));

        fade();

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
                _photoToolbar.setSlideshowPlaying(true);
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
        fade(_photoToolbar);
        fade(_thumbnailRecyclerView);
    }


    private void fade(View view) {
        AlphaAnimation alpha = new AlphaAnimation(FADE_START_ALPHA, FADE_END_ALPHA);
        alpha.setDuration(FADE_DURATION);
        alpha.setFillAfter(true);

        view.startAnimation(alpha);
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
        _photoPager.rotateImage(direction);
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
                        this::onGetPhotoList,
                        ex -> _authHandler.handleException(ex)
                )
        );

        updateProgress();
    }



    private void onGetPhotoList(List<Photo> list) {
        _index = 0;
        _photoList.addAll(list);

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
                        this::onGetRandom,
                        ex -> _authHandler.handleException(ex)
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

        Log.d(MawApplication.LOG_TAG, "random photo: " + result.getPhoto().getId());

        onRandomPhotoFetched();
    }


    private void onRandomPhotoFetched() {
        _photoPagerAdapter.notifyDataSetChanged();

        if (!_displayedRandomImage) {
            _displayedRandomImage = true;
            gotoPhoto(0);
        }

        updateProgress();
    }


    private void onGatherPhotoListComplete() {
        _photoPagerAdapter.notifyDataSetChanged();

        gotoPhoto(_index);

        updateProgress();
    }


    private void startSlideshow() {
        if (_slideshowExecutor == null) {
            int intervalSeconds = Integer.parseInt(_sharedPrefs.getString("slideshow_interval", "3"));

            _slideshowExecutor = new ScheduledThreadPoolExecutor(1);
            _slideshowExecutor.scheduleWithFixedDelay(this::incrementSlideshow, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        }
    }


    private void incrementSlideshow() {
        int nextIndex = _index + 1;

        if (nextIndex < _photoList.size()) {
            SlideshowRunnable slideshowRunnable = new SlideshowRunnable((nextIndex));
            _theActivity.runOnUiThread(slideshowRunnable);
        } else {
            _theActivity.runOnUiThread(this::toggleSlideshow);
        }
    }


    private void stopSlideshow() {
        if (_slideshowExecutor != null) {
            _slideshowExecutor.shutdownNow();
            _slideshowExecutor = null;

            _photoToolbar.setSlideshowPlaying(false);
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
        _photoPager.setCurrentItem(_index);
        updateThumbnail();

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
                        ex -> _authHandler.handleException(ex)
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
        displayView(_thumbnailRecyclerView, doShow);
    }


    private void displayToolbar(boolean doShow) { displayView(_photoToolbar, doShow); }


    private void displayView(View view, boolean doShow) {
        int visibility = doShow ? View.VISIBLE : View.INVISIBLE;

        view.setVisibility(visibility);
    }


    private void updateThumbnail() {
        Photo thumb = getPhotoList().get(getCurrentIndex());

        // TODO: scroll to current photo
        /*
        _horizontalScrollView.smoothScrollTo(thumb.getLeft(), 0);

        // TODO: consider adding generic version to a function to the photolistactivity
        // we force the animation here to leave the alpha at 0.2, otherwise was resetting to 1.0
        AlphaAnimation alpha = new AlphaAnimation(PhotoListActivity.FADE_END_ALPHA, PhotoListActivity.FADE_END_ALPHA);
        alpha.setDuration(PhotoListActivity.FADE_DURATION);
        alpha.setFillAfter(true);

        _horizontalScrollView.startAnimation(alpha);
        */
    }


    private class SlideshowRunnable implements Runnable {
        private final int _nextIndex;

        SlideshowRunnable(int nextIndex) {
            _nextIndex = nextIndex;
        }

        @Override
        public void run() {
            gotoPhoto(_nextIndex);
        }
    }
}