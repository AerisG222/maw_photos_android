package us.mikeandwan.photos.ui.photos;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.prefs.PhotoDisplayPreference;
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
    private static final float FADE_END_ALPHA = 0.2f;
    private static final int FADE_DURATION = 3000;

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
    private String _name;
    private MenuItem _menuShare;
    private TaskComponent _taskComponent;

    @BindView(R.id.progressBar) ProgressBar _progressBar;
    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.photoToolbar) ConstraintLayout _photoToolbar;
    @BindView(R.id.commentButton) ImageButton _commentButton;
    @BindView(R.id.exifButton) ImageButton _exifButton;
    @BindView(R.id.rotateLeftButton) ImageButton _rotateLeftButton;
    @BindView(R.id.rotateRightButton) ImageButton _rotateRightButton;
    @BindView(R.id.ratingButton) ImageButton _ratingButton;
    @BindView(R.id.slideshowButton)  ImageButton _slideshowButton;
    @BindView(R.id.photoPager) PhotoViewPager _photoPager;
    @BindView(R.id.thumbnailPhotoRecycler) RecyclerView _thumbnailRecyclerView;

    @OnClick(R.id.exifButton) void onExifButtonClick() {
        showExif();
    }
    @OnClick(R.id.ratingButton) void onRatingButtonClick() {
        showRating();
    }
    @OnClick(R.id.commentButton) void onCommentButtonClick() { showComments(); }
    @OnClick(R.id.rotateLeftButton) void onRotateLeftButtonClick() { rotatePhoto(-1); }
    @OnClick(R.id.rotateRightButton) void onRotateRightButtonClick() { rotatePhoto(1); }
    @OnClick(R.id.slideshowButton) void onSlideshowButtonClick() { toggleSlideshow(); }

    @Inject PhotoDisplayPreference _photoPrefs;
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

        _photoPager.setAdapter(_photoPagerAdapter);
        _photoPager.onPhotoSelected().subscribe(this::gotoPhoto);

        _url = getIntent().getStringExtra("URL");
        _name = getIntent().getStringExtra("NAME");
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
        disposables.clear();
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

        layoutActivity();

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
            int intervalSeconds = _photoPrefs.getSlideshowIntervalInSeconds();

            _slideshowExecutor = new ScheduledThreadPoolExecutor(1);
            _slideshowExecutor.scheduleWithFixedDelay(this::incrementSlideshow, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
            _slideshowButton.setImageResource(R.drawable.ic_stop_white_24dp);
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
            _slideshowButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
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


    private void displayView(View view, boolean doShow) {
        int visibility = doShow ? View.VISIBLE : View.GONE;

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


    private void layoutActivity() {
        displayView(_toolbar, _photoPrefs.getDoDisplayTopToolbar());
        displayView(_photoToolbar, _photoPrefs.getDoDisplayPhotoToolbar());
        displayView(_thumbnailRecyclerView, _photoPrefs.getDoDisplayThumbnails());

        if(_photoPrefs.getDoDisplayTopToolbar()) {
            updateToolbar(_toolbar, String.valueOf(_name));
        }

        if(_photoPrefs.getDoDisplayThumbnails()) {
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
            _thumbnailRecyclerView.setLayoutManager(llm);

            _thumbnailRecyclerAdapter.setPhotoList(_photoList);
            _thumbnailRecyclerView.setAdapter(_thumbnailRecyclerAdapter);

            if(_photoPrefs.getDoFadeControls()) {
                _thumbnailRecyclerView.setOnTouchListener((view, event) -> {
                    fade(_thumbnailRecyclerView);
                    return false;
                });
            }
        }

        if(_photoPrefs.getDoFadeControls()) {
            // if fade is enabled, we just fade the controls and let the photo / viewpager
            // occupy the full screen, as it will be visible under controls
            fade();
        }
        else {
            // if we do not fade the controls, then we must reconfigure the photo layout to
            // be within the controls that are displayed

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
