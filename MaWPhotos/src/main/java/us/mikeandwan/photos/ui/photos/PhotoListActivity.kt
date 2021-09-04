package us.mikeandwan.photos.ui.photos

import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.ui.BaseActivity
import us.mikeandwan.photos.ui.photos.IPhotoActivity
import io.reactivex.disposables.CompositeDisposable
import us.mikeandwan.photos.services.PhotoListType
import butterknife.BindView
import us.mikeandwan.photos.R
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.ProgressBar
import android.widget.ImageButton
import us.mikeandwan.photos.ui.photos.PhotoViewPager
import androidx.recyclerview.widget.RecyclerView
import butterknife.OnClick
import javax.inject.Inject
import us.mikeandwan.photos.prefs.PhotoDisplayPreference
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.ui.photos.FullScreenImageAdapter
import us.mikeandwan.photos.ui.photos.ThumbnailRecyclerAdapter
import android.os.Bundle
import us.mikeandwan.photos.ui.photos.PhotoListActivity
import butterknife.ButterKnife
import android.content.Intent
import us.mikeandwan.photos.ui.settings.SettingsActivity
import us.mikeandwan.photos.ui.receiver.PhotoReceiverActivity
import us.mikeandwan.photos.ui.photos.RatingDialogFragment
import us.mikeandwan.photos.ui.photos.ExifDialogFragment
import us.mikeandwan.photos.ui.photos.CommentDialogFragment
import timber.log.Timber
import io.reactivex.Flowable
import us.mikeandwan.photos.models.ApiCollection
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import androidx.core.view.MenuItemCompat
import us.mikeandwan.photos.models.PhotoSize
import us.mikeandwan.photos.ui.photos.PhotoListActivity.SlideshowRunnable
import us.mikeandwan.photos.ui.photos.ThumbnailLinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View.OnTouchListener
import androidx.constraintlayout.widget.ConstraintSet
import android.animation.ObjectAnimator
import android.app.DialogFragment
import android.view.*
import androidx.appcompat.widget.ShareActionProvider
import androidx.appcompat.widget.Toolbar
import us.mikeandwan.photos.models.Photo
import java.util.ArrayList
import java.util.HashSet
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@AndroidEntryPoint
class PhotoListActivity : BaseActivity(), IPhotoActivity {
    private val _disposables = CompositeDisposable()
    private var _slideshowExecutor: ScheduledThreadPoolExecutor? = null
    private var _randomPhotoIds: HashSet<Int>? = null
    private val _taskCount = AtomicInteger(0)
    private var _type: PhotoListType? = null
    private var _name: String? = null
    private var _categoryId = 0
    private var _menuShare: MenuItem? = null

    @JvmField
    @BindView(R.id.container)
    var _container: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.progressBar)
    var _progressBar: ProgressBar? = null

    @JvmField
    @BindView(R.id.toolbar)
    var _toolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.photoToolbar)
    var _photoToolbar: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.commentButton)
    var _commentButton: ImageButton? = null

    @JvmField
    @BindView(R.id.exifButton)
    var _exifButton: ImageButton? = null

    @JvmField
    @BindView(R.id.rotateLeftButton)
    var _rotateLeftButton: ImageButton? = null

    @JvmField
    @BindView(R.id.rotateRightButton)
    var _rotateRightButton: ImageButton? = null

    @JvmField
    @BindView(R.id.ratingButton)
    var _ratingButton: ImageButton? = null

    @JvmField
    @BindView(R.id.slideshowButton)
    var _slideshowButton: ImageButton? = null

    @JvmField
    @BindView(R.id.photoPager)
    var _photoPager: PhotoViewPager? = null

    @JvmField
    @BindView(R.id.thumbnailPhotoRecycler)
    var _thumbnailRecyclerView: RecyclerView? = null

    @OnClick(R.id.exifButton)
    fun onExifButtonClick() {
        showExif()
    }

    @OnClick(R.id.ratingButton)
    fun onRatingButtonClick() {
        showRating()
    }

    @OnClick(R.id.commentButton)
    fun onCommentButtonClick() {
        showComments()
    }

    @OnClick(R.id.rotateLeftButton)
    fun onRotateLeftButtonClick() {
        rotatePhoto(-1)
    }

    @OnClick(R.id.rotateRightButton)
    fun onRotateRightButtonClick() {
        rotatePhoto(1)
    }

    @OnClick(R.id.slideshowButton)
    fun onSlideshowButtonClick() {
        toggleSlideshow()
    }

    @Inject lateinit var _photoPrefs: PhotoDisplayPreference
    @Inject lateinit var _dataServices: DataServices
    @Inject lateinit var _photoPagerAdapter: FullScreenImageAdapter
    @Inject lateinit var _thumbnailRecyclerAdapter: ThumbnailRecyclerAdapter

    private var _index = 0
    private var _isRandomView = false
    private var _displayedRandomImage = false
    private var _playingSlideshow = false
    private var _photoList: ArrayList<Photo>? = ArrayList()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _type = PhotoListType.valueOf(intent.getStringExtra("TYPE")!!)
        _name = intent.getStringExtra("NAME")
        _categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        if (savedInstanceState != null) {
            _index = savedInstanceState.getInt(STATE_INDEX)
            _isRandomView = savedInstanceState.getBoolean(STATE_IS_RANDOM_VIEW)
            _displayedRandomImage = savedInstanceState.getBoolean(STATE_DISPLAY_RANDOM_IMAGE)
            _playingSlideshow = savedInstanceState.getBoolean(STATE_PLAYING_SLIDESHOW)
            _photoList = savedInstanceState.getSerializable(STATE_PHOTO_LIST) as ArrayList<Photo>?
        }
        setContentView(R.layout.activity_photo_list)
        ButterKnife.bind(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.photo_list, menu)
        _menuShare = menu.findItem(R.id.action_share)
        return true
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_INDEX, _index)
        outState.putBoolean(STATE_IS_RANDOM_VIEW, _isRandomView)
        outState.putBoolean(STATE_DISPLAY_RANDOM_IMAGE, _displayedRandomImage)
        outState.putBoolean(STATE_PLAYING_SLIDESHOW, _playingSlideshow)
        outState.putSerializable(STATE_PHOTO_LIST, _photoList)
    }

    override fun onDestroy() {
        _disposables.clear()
        _thumbnailRecyclerAdapter!!.dispose()
        _photoPagerAdapter!!.dispose()
        super.onDestroy()
    }

    public override fun onResume() {
        super.onResume()
        _photoPagerAdapter!!.refreshPhotoList()
        _thumbnailRecyclerAdapter!!.refreshPhotoList()
        layoutActivity()
        _photoPager!!.adapter = _photoPagerAdapter
        _disposables.add(
            _photoPager!!.onPhotoSelected().subscribe { index: Int -> gotoPhoto(index) })

        // if we are coming back from an orientation change, we might already have a valid list
        // populated.  if so, use the original list.
        if (_photoList!!.isEmpty()) {
            if (_type == PhotoListType.Random) {
                _isRandomView = true
                initRandomPhotos()
            } else {
                initPhotoList()
            }
        } else {
            if (_playingSlideshow) {
                startSlideshow()
            }
            onGatherPhotoListComplete()
        }
    }

    public override fun onPause() {
        // make sure we kill the slideshow thread if we are leaving the activity to avoid errors
        stopSlideshow()
        super.onPause()
    }

    fun onMenuItemSettings(menuItem: MenuItem?) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun onViewUploadQueueClick(menuItem: MenuItem?) {
        val intent = Intent(this, PhotoReceiverActivity::class.java)
        startActivity(intent)
    }

    override val currentPhoto: Photo
        get() = _photoList!![_index]
    override val photoList: List<Photo>?
        get() = _photoList

    override fun addWork() {
        _taskCount.incrementAndGet()
        updateProgress()
    }

    override fun removeWork() {
        _taskCount.decrementAndGet()
        updateProgress()
    }

    fun showRating() {
        showDialog(RatingDialogFragment())
    }

    fun showExif() {
        showDialog(ExifDialogFragment())
    }

    fun showComments() {
        showDialog(CommentDialogFragment())
    }

    fun rotatePhoto(direction: Int) {
        _photoPager!!.rotateImage(direction)
    }

    fun toggleSlideshow() {
        _playingSlideshow = if (_slideshowExecutor != null) {
            stopSlideshow()
            false
        } else {
            startSlideshow()
            true
        }
    }

    private fun updateProgress() {
        runOnUiThread {
            val count = _taskCount.get()
            Timber.d("task count: %d", count)
            if (count > 0) {
                _progressBar!!.visibility = View.VISIBLE
            } else {
                _progressBar!!.visibility = View.INVISIBLE
            }
        }
    }

    private fun initPhotoList() {
        _disposables.add(Flowable.fromCallable {
            addWork()
            _dataServices!!.getPhotoList(_type, _categoryId)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: ApiCollection<Photo> ->
                    removeWork()
                    onGetPhotoList(x.items)
                }
            ) { ex: Throwable? ->
                removeWork()
                handleApiException(ex)
            }
        )
    }

    private fun onGetPhotoList(list: List<Photo>) {
        _index = 0
        _photoList!!.addAll(list)
        onGatherPhotoListComplete()
    }

    private fun initRandomPhotos() {
        _randomPhotoIds = HashSet()
        _disposables.add(Flowable.fromCallable {
            addWork()
            _dataServices!!.getRandomPhotos(RANDOM_INITIAL_COUNT)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: ApiCollection<Photo> ->
                    removeWork()
                    for (p in x.items) {
                        onGetRandom(p)
                    }
                }
            ) { ex: Throwable? ->
                removeWork()
                handleApiException(ex)
            }
        )
    }

    private fun fetchRandom() {
        _disposables.add(Flowable.fromCallable {
            addWork()
            _dataServices!!.randomPhoto
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: Photo ->
                    removeWork()
                    onGetRandom(x)
                }
            ) { ex: Throwable? ->
                removeWork()
                handleApiException(ex)
            }
        )
    }

    private fun onGetRandom(result: Photo) {
        if (_randomPhotoIds!!.contains(result.id)) {
            // avoid duplicates
            return
        }
        _randomPhotoIds!!.add(result.id)
        _index = 0
        _photoList!!.add(result)
        Timber.d("random photo: %s", result.id)
        onRandomPhotoFetched()
    }

    private fun onRandomPhotoFetched() {
        _photoPagerAdapter!!.notifyDataSetChanged()
        _thumbnailRecyclerAdapter!!.notifyDataSetChanged()
        if (!_displayedRandomImage) {
            _displayedRandomImage = true
            gotoPhoto(0)
        }
    }

    private fun onGatherPhotoListComplete() {
        _photoPagerAdapter!!.notifyDataSetChanged()
        _thumbnailRecyclerAdapter!!.notifyDataSetChanged()
        gotoPhoto(_index)
    }

    fun gotoPhoto(index: Int) {
        _index = index
        displayMainImage(_photoList!![_index])

        // try to leave a buffer of 5 images from where the user is to the end of the list
        if (_isRandomView) {
            val minEndingIndex = index + RANDOM_INITIAL_COUNT
            if (minEndingIndex > _photoList!!.size) {
                for (i in _photoList!!.size until minEndingIndex) {
                    fetchRandom()
                }
            }
        }
    }

    private fun displayMainImage(photo: Photo) {
        _photoPager!!.currentItem = _index
        _thumbnailRecyclerView!!.scrollToPosition(_index)
        val sap = MenuItemCompat.getActionProvider(_menuShare) as ShareActionProvider
        sap?.setShareIntent(createShareIntent(photo))
        prefetchMainImage(_index)
    }

    private fun prefetchMainImage(index: Int) {
        run {
            var i = index + 1
            while (i < index + PREFETCH_COUNT && i < _photoList!!.size) {
                prefetchImage(_photoList!![i], PhotoSize.Md)
                i++
            }
        }
        var i = index - 1
        while (i > index - PREFETCH_COUNT && i > 0) {
            prefetchImage(_photoList!![i], PhotoSize.Md)
            i--
        }
    }

    private fun prefetchImage(photo: Photo, size: PhotoSize) {
        _disposables.add(Flowable.fromCallable {
            addWork()
            _dataServices!!.downloadPhoto(photo, size)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: String? -> removeWork() }
            ) { ex: Throwable? ->
                removeWork()
                handleApiException(ex)
            }
        )
    }

    private fun showDialog(fragment: DialogFragment) {
        ensureSlideshowStopped()
        val ft = fragmentManager.beginTransaction()
        val prev = fragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        fragment.show(ft, "dialog")
    }

    private fun startSlideshow() {
        if (_slideshowExecutor == null) {
            val intervalSeconds = _photoPrefs!!.slideshowIntervalInSeconds
            _slideshowExecutor = ScheduledThreadPoolExecutor(1)
            _slideshowExecutor!!.scheduleWithFixedDelay(
                { incrementSlideshow() },
                intervalSeconds.toLong(),
                intervalSeconds.toLong(),
                TimeUnit.SECONDS
            )
            _slideshowButton!!.setImageResource(R.drawable.ic_stop_white_24dp)
        }
    }

    private fun incrementSlideshow() {
        val nextIndex = _index + 1
        if (nextIndex < _photoList!!.size) {
            val slideshowRunnable = SlideshowRunnable(nextIndex)
            runOnUiThread(slideshowRunnable)
        } else {
            runOnUiThread { toggleSlideshow() }
        }
    }

    private fun stopSlideshow() {
        if (_slideshowExecutor != null) {
            _slideshowExecutor!!.shutdownNow()
            _slideshowExecutor = null
            _slideshowButton!!.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        }
    }

    private fun ensureSlideshowStopped() {
        if (_slideshowExecutor != null) {
            toggleSlideshow()
        }
    }

    private fun layoutActivity() {
        displayView(_toolbar, _photoPrefs!!.doDisplayTopToolbar)
        displayView(_photoToolbar, _photoPrefs!!.doDisplayPhotoToolbar)
        displayView(_thumbnailRecyclerView, _photoPrefs!!.doDisplayThumbnails)
        if (_photoPrefs!!.doDisplayTopToolbar) {
            updateToolbar(_toolbar, _name.toString())
        }
        if (_photoPrefs!!.doDisplayThumbnails) {
            val llm =
                ThumbnailLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false, _index)
            _thumbnailRecyclerView!!.setHasFixedSize(true)
            _thumbnailRecyclerView!!.layoutManager = llm
            _thumbnailRecyclerView!!.adapter = _thumbnailRecyclerAdapter
            _disposables.add(
                _thumbnailRecyclerAdapter!!.onThumbnailSelected()
                    .subscribe { index: Int -> gotoPhoto(index) })
            if (_photoPrefs!!.doFadeControls) {
                _thumbnailRecyclerView!!.setOnTouchListener { view: View?, event: MotionEvent? ->
                    fade(_thumbnailRecyclerView)
                    false
                }
            }
        }
        if (!_photoPrefs!!.doFadeControls) {
            val set = ConstraintSet()
            set.constrainHeight(R.id.photoPager, 0)
            set.constrainWidth(R.id.photoPager, 0)
            set.connect(R.id.photoPager, ConstraintSet.LEFT, R.id.container, ConstraintSet.LEFT, 0)
            set.connect(
                R.id.photoPager,
                ConstraintSet.RIGHT,
                R.id.container,
                ConstraintSet.RIGHT,
                0
            )

            // if we do not updateOpacity the controls, then we must reconfigure the photo layout to
            // be within the controls that are displayed
            if (_toolbar!!.isShown) {
                set.connect(
                    R.id.photoPager,
                    ConstraintSet.TOP,
                    R.id.toolbar,
                    ConstraintSet.BOTTOM,
                    0
                )
            } else {
                set.connect(
                    R.id.photoPager,
                    ConstraintSet.TOP,
                    R.id.container,
                    ConstraintSet.TOP,
                    0
                )
            }
            if (_photoToolbar!!.isShown) {
                set.connect(
                    R.id.photoPager,
                    ConstraintSet.BOTTOM,
                    R.id.photoToolbar,
                    ConstraintSet.TOP,
                    0
                )
            } else if (_thumbnailRecyclerView!!.isShown) {
                set.connect(
                    R.id.photoPager,
                    ConstraintSet.BOTTOM,
                    R.id.thumbnailPhotoRecycler,
                    ConstraintSet.TOP,
                    0
                )
            } else {
                set.connect(
                    R.id.photoPager,
                    ConstraintSet.BOTTOM,
                    R.id.container,
                    ConstraintSet.BOTTOM,
                    0
                )
            }
            set.applyTo(_container)
        }
        updateOpacity()
    }

    private fun displayView(view: View?, doShow: Boolean) {
        val visibility = if (doShow) View.VISIBLE else View.GONE
        view!!.visibility = visibility
    }

    private fun updateOpacity() {
        if (_photoPrefs!!.doFadeControls) {
            fade(_toolbar)
            fade(_photoToolbar)
            fade(_thumbnailRecyclerView)
        } else {
            appear(_toolbar)
            appear(_photoToolbar)
            appear(_thumbnailRecyclerView)
        }
    }

    private fun appear(view: View?) {
        view!!.clearAnimation()
        view.alpha = FADE_START_ALPHA
    }

    private fun fade(view: View?) {
        val anim = ObjectAnimator.ofFloat(view, "alpha", FADE_START_ALPHA, FADE_END_ALPHA)
        anim.duration = FADE_DURATION.toLong()
        anim.start()
    }

    private fun createShareIntent(photo: Photo?): Intent? {
        if (photo != null) {
            val contentUri = _dataServices!!.getSharingContentUri(photo.imageMd.url)
            if (contentUri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.setDataAndType(contentUri, "image/*")
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                return shareIntent
            }
        }
        return null
    }

    private inner class SlideshowRunnable internal constructor(private val _nextIndex: Int) :
        Runnable {
        override fun run() {
            gotoPhoto(_nextIndex)
        }
    }

    companion object {
        private const val FADE_START_ALPHA = 1.0f
        private const val FADE_END_ALPHA = 0.2f
        private const val FADE_DURATION = 3000
        private const val RANDOM_INITIAL_COUNT = 20
        private const val PREFETCH_COUNT = 2
        private const val STATE_INDEX = "index"
        private const val STATE_IS_RANDOM_VIEW = "is_random_view"
        private const val STATE_DISPLAY_RANDOM_IMAGE = "display_random_image"
        private const val STATE_PLAYING_SLIDESHOW = "playing_slideshow"
        private const val STATE_PHOTO_LIST = "photo_list"
    }
}