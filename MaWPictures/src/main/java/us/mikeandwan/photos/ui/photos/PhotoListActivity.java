package us.mikeandwan.photos.ui.photos;

import android.animation.ObjectAnimator;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.di.DaggerActivityComponent;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.prefs.PhotoDisplayPreference;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.services.PhotoListType;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.settings.SettingsActivity;

import static android.support.constraint.ConstraintSet.BOTTOM;
import static android.support.constraint.ConstraintSet.LEFT;
import static android.support.constraint.ConstraintSet.RIGHT;
import static android.support.constraint.ConstraintSet.TOP;


public class PhotoListActivity extends BaseActivity implements IPhotoActivity, HasComponent<ActivityComponent> {
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

    private final CompositeDisposable _disposables = new CompositeDisposable();
    private ScheduledThreadPoolExecutor _slideshowExecutor;
    private HashSet<Integer> _randomPhotoIds;
    private AtomicInteger _taskCount = new AtomicInteger(0);
    private PhotoListType _type;
    private String _name;
    private int _categoryId;
    private MenuItem _menuShare;
    private ActivityComponent _activityComponent;

    @BindView(R.id.container) ConstraintLayout _container;
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
    @Inject AuthenticationExceptionHandler _authHandler;
    @Inject DataServices _dataServices;
    @Inject FullScreenImageAdapter _photoPagerAdapter;
    @Inject ThumbnailRecyclerAdapter _thumbnailRecyclerAdapter;

    private int _index = 0;
    private boolean _isRandomView;
    private boolean _displayedRandomImage;
    private boolean _playingSlideshow;
    private ArrayList<Photo> _photoList = new ArrayList<>();


    public ActivityComponent getComponent() {
        return _activityComponent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _type = PhotoListType.valueOf(getIntent().getStringExtra("TYPE"));
        _name = getIntent().getStringExtra("NAME");
        _categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);

        if(savedInstanceState != null) {
            _index = savedInstanceState.getInt(STATE_INDEX);
            _isRandomView = savedInstanceState.getBoolean(STATE_IS_RANDOM_VIEW);
            _displayedRandomImage = savedInstanceState.getBoolean(STATE_DISPLAY_RANDOM_IMAGE);
            _playingSlideshow = savedInstanceState.getBoolean(STATE_PLAYING_SLIDESHOW);
            _photoList = (ArrayList<Photo>) savedInstanceState.getSerializable(STATE_PHOTO_LIST);
        }

        setContentView(R.layout.activity_photo_list);
        ButterKnife.bind(this);

        _activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        _activityComponent.inject(this);
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
        _disposables.clear();
        _thumbnailRecyclerAdapter.dispose();
        _photoPagerAdapter.dispose();

        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();

        _photoPagerAdapter.refreshPhotoList();
        _thumbnailRecyclerAdapter.refreshPhotoList();

        layoutActivity();

        _photoPager.setAdapter(_photoPagerAdapter);
        _photoPager.onPhotoSelected().subscribe(this::gotoPhoto);

        // if we are coming back from an orientation change, we might already have a valid list
        // populated.  if so, use the original list.
        if(_photoList.isEmpty()) {
            if (_type == PhotoListType.Random) {
                _isRandomView = true;
                initRandomPhotos();
            } else {
                initPhotoList();
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
        // make sure we kill the slideshow thread if we are leaving the activity to avoid errors
        stopSlideshow();

        super.onPause();
    }


    public void onMenuItemSettings(MenuItem menuItem) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public Photo getCurrentPhoto() {
        return _photoList.get(_index);
    }


    public List<Photo> getPhotoList() {
        return _photoList;
    }


    public void addWork() {
        _taskCount.incrementAndGet();
        updateProgress();
    }


    public void removeWork() {
        _taskCount.decrementAndGet();
        updateProgress();
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
            _playingSlideshow = false;
        } else {
            startSlideshow();
            _playingSlideshow = true;
        }
    }


    private void updateProgress() {
        runOnUiThread(() -> {
            int count = _taskCount.get();

            Log.d(MawApplication.LOG_TAG, "task count: " + count);

            if (count > 0) {
                _progressBar.setVisibility(View.VISIBLE);
            } else {
                _progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void initPhotoList() {
        _disposables.add(Flowable.fromCallable(() -> {
                    addWork();
                    return _dataServices.getPhotoList(_type, _categoryId);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> {
                            removeWork();
                            onGetPhotoList(x);
                        },
                        ex -> {
                            removeWork();
                            _authHandler.handleException(ex);
                        }
                )
        );
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
        _disposables.add(Flowable.fromCallable(() -> {
                    addWork();
                    return _dataServices.getRandomPhoto();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> {
                            removeWork();
                            onGetRandom(x);
                        },
                        ex -> {
                            removeWork();
                            _authHandler.handleException(ex);
                        }
                )
        );
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
        _thumbnailRecyclerAdapter.notifyDataSetChanged();

        if (!_displayedRandomImage) {
            _displayedRandomImage = true;
            gotoPhoto(0);
        }
    }


    private void onGatherPhotoListComplete() {
        _photoPagerAdapter.notifyDataSetChanged();
        _thumbnailRecyclerAdapter.notifyDataSetChanged();

        gotoPhoto(_index);
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


    private void displayMainImage(Photo photo) {
        _photoPager.setCurrentItem(_index);
        _thumbnailRecyclerView.scrollToPosition(_index);

        ShareActionProvider sap = (ShareActionProvider) MenuItemCompat.getActionProvider(_menuShare);

        if(sap != null) {
            sap.setShareIntent(createShareIntent(photo));
        }

        prefetchMainImage(_index);
    }


    private void prefetchMainImage(int index) {
        for (int i = index + 1; i < index + PREFETCH_COUNT && i < _photoList.size(); i++) {
            prefetchImage(_photoList.get(i), PhotoSize.Md);
        }

        for (int i = index - 1; i > index - PREFETCH_COUNT && i > 0; i--) {
            prefetchImage(_photoList.get(i), PhotoSize.Md);
        }
    }


    private void prefetchImage(final Photo photo, PhotoSize size) {
        _disposables.add(Flowable.fromCallable(() -> {
                    addWork();
                    return _dataServices.downloadPhoto(photo, size);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> removeWork(),
                        ex -> {
                            removeWork();
                            _authHandler.handleException(ex);
                        }
                )
        );
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
            this.runOnUiThread(slideshowRunnable);
        } else {
            this.runOnUiThread(this::toggleSlideshow);
        }
    }


    private void stopSlideshow() {
        if (_slideshowExecutor != null) {
            _slideshowExecutor.shutdownNow();
            _slideshowExecutor = null;
            _slideshowButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }


    private void ensureSlideshowStopped() {
        if (_slideshowExecutor != null) {
            toggleSlideshow();
        }
    }


    private void layoutActivity() {
        displayView(_toolbar, _photoPrefs.getDoDisplayTopToolbar());
        displayView(_photoToolbar, _photoPrefs.getDoDisplayPhotoToolbar());
        displayView(_thumbnailRecyclerView, _photoPrefs.getDoDisplayThumbnails());

        if(_photoPrefs.getDoDisplayTopToolbar()) {
            updateToolbar(_toolbar, String.valueOf(_name));
        }

        if(_photoPrefs.getDoDisplayThumbnails()) {
            ThumbnailLinearLayoutManager llm = new ThumbnailLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false, _index);

            _thumbnailRecyclerView.setHasFixedSize(true);
            _thumbnailRecyclerView.setLayoutManager(llm);
            _thumbnailRecyclerView.setAdapter(_thumbnailRecyclerAdapter);
            _thumbnailRecyclerAdapter.onThumbnailSelected().subscribe(this::gotoPhoto);

            if(_photoPrefs.getDoFadeControls()) {
                _thumbnailRecyclerView.setOnTouchListener((view, event) -> {
                    fade(_thumbnailRecyclerView);
                    return false;
                });
            }
        }

        if(!_photoPrefs.getDoFadeControls()) {
            ConstraintSet set = new ConstraintSet();

            set.constrainHeight(R.id.photoPager, 0);
            set.constrainWidth(R.id.photoPager, 0);

            set.connect(R.id.photoPager, LEFT, R.id.container, LEFT, 0);
            set.connect(R.id.photoPager, RIGHT, R.id.container, RIGHT, 0);

            // if we do not updateOpacity the controls, then we must reconfigure the photo layout to
            // be within the controls that are displayed
            if(_toolbar.isShown()) {
                set.connect(R.id.photoPager, TOP, R.id.toolbar, BOTTOM, 0);
            }
            else {
                set.connect(R.id.photoPager, TOP, R.id.container, TOP, 0);
            }

            if(_photoToolbar.isShown()) {
                set.connect(R.id.photoPager, BOTTOM, R.id.photoToolbar, TOP, 0);
            }
            else if(_thumbnailRecyclerView.isShown()) {
                set.connect(R.id.photoPager, BOTTOM, R.id.thumbnailPhotoRecycler, TOP, 0);
            }
            else {
                set.connect(R.id.photoPager, BOTTOM, R.id.container, BOTTOM, 0);
            }

            set.applyTo(_container);
        }

        updateOpacity();
    }


    private void displayView(View view, boolean doShow) {
        int visibility = doShow ? View.VISIBLE : View.GONE;

        view.setVisibility(visibility);
    }


    private void updateOpacity() {
        if(_photoPrefs.getDoFadeControls()) {
            fade(_toolbar);
            fade(_photoToolbar);
            fade(_thumbnailRecyclerView);
        }
        else {
            appear(_toolbar);
            appear(_photoToolbar);
            appear(_thumbnailRecyclerView);
        }
    }


    private void appear(View view) {
        view.clearAnimation();
        view.setAlpha(FADE_START_ALPHA);
    }


    private void fade(View view) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", FADE_START_ALPHA, FADE_END_ALPHA);
        anim.setDuration(FADE_DURATION);
        anim.start();
    }


    private Intent createShareIntent(Photo photo) {
        if (photo != null) {
            Uri contentUri = _dataServices.getSharingContentUri(photo.getMdInfo().getPath());

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
