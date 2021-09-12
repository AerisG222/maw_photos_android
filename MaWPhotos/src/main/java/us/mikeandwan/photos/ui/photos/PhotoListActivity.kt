package us.mikeandwan.photos.ui.photos

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.ShareActionProvider
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.ActivityPhotoListBinding
import us.mikeandwan.photos.models.ApiCollection
import us.mikeandwan.photos.models.Photo
import us.mikeandwan.photos.models.PhotoSize
import us.mikeandwan.photos.prefs.PhotoDisplayPreference
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.services.PhotoListType
import us.mikeandwan.photos.ui.BaseActivity
import us.mikeandwan.photos.ui.receiver.PhotoReceiverActivity
import us.mikeandwan.photos.ui.settings.SettingsActivity
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

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

    private lateinit var binding: ActivityPhotoListBinding

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
        binding = ActivityPhotoListBinding.inflate(layoutInflater)

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

        setContentView(binding.root)
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
        _thumbnailRecyclerAdapter.dispose()
        _photoPagerAdapter.dispose()
        super.onDestroy()
    }

    public override fun onResume() {
        super.onResume()
        _photoPagerAdapter.refreshPhotoList()
        _thumbnailRecyclerAdapter.refreshPhotoList()
        layoutActivity()
        binding.photoPager.adapter = _photoPagerAdapter
        _disposables.add(
            binding.photoPager.onPhotoSelected().subscribe { index: Int -> gotoPhoto(index) })

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

    fun showRating(view: View) {
        showDialog(RatingDialogFragment())
    }

    fun showExif(view: View) {
        showDialog(ExifDialogFragment())
    }

    fun showComments(view: View) {
        showDialog(CommentDialogFragment())
    }

    fun rotatePhotoLeft(view: View) {
        rotatePhoto(-1)
    }

    fun rotatePhotoRight(view: View) {
        rotatePhoto(1)
    }

    fun rotatePhoto(direction: Int) {
        binding.photoPager.rotateImage(direction)
    }

    fun toggleSlideshow(view: View) {
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
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun initPhotoList() {
        _disposables.add(Flowable.fromCallable {
            addWork()
            _dataServices.getPhotoList(_type!!, _categoryId)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: ApiCollection<Photo>? ->
                    removeWork()
                    onGetPhotoList(x!!.items)
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
            _dataServices.getRandomPhotos(RANDOM_INITIAL_COUNT)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: ApiCollection<Photo>? ->
                    removeWork()
                    for (p in x!!.items) {
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
            _dataServices.getRandomPhoto()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: Photo? ->
                    removeWork()
                    onGetRandom(x!!)
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
        _photoPagerAdapter.notifyDataSetChanged()
        _thumbnailRecyclerAdapter.notifyDataSetChanged()
        if (!_displayedRandomImage) {
            _displayedRandomImage = true
            gotoPhoto(0)
        }
    }

    private fun onGatherPhotoListComplete() {
        _photoPagerAdapter.notifyDataSetChanged()
        _thumbnailRecyclerAdapter.notifyDataSetChanged()
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
        binding.photoPager.currentItem = _index
        binding.thumbnailPhotoRecycler.scrollToPosition(_index)
        val sap = MenuItemCompat.getActionProvider(_menuShare) as ShareActionProvider
        sap.setShareIntent(createShareIntent(photo))
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
            _dataServices.downloadPhoto(photo, size)
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

        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")

        if (prev != null) {
            ft.remove(prev)
        }

        ft.addToBackStack(null)
        fragment.show(ft, "dialog")
    }

    private fun startSlideshow() {
        if (_slideshowExecutor == null) {
            val intervalSeconds = _photoPrefs.slideshowIntervalInSeconds
            _slideshowExecutor = ScheduledThreadPoolExecutor(1)

            _slideshowExecutor!!.scheduleWithFixedDelay(
                { incrementSlideshow() },
                intervalSeconds.toLong(),
                intervalSeconds.toLong(),
                TimeUnit.SECONDS
            )

            binding.slideshowButton.setImageResource(R.drawable.ic_stop_white_24dp)
        }
    }

    private fun incrementSlideshow() {
        val nextIndex = _index + 1

        if (nextIndex < _photoList!!.size) {
            val slideshowRunnable = SlideshowRunnable(nextIndex)
            runOnUiThread(slideshowRunnable)
        } else {
            runOnUiThread { toggleSlideshow(binding.root) }
        }
    }

    private fun stopSlideshow() {
        if (_slideshowExecutor != null) {
            _slideshowExecutor!!.shutdownNow()
            _slideshowExecutor = null
            binding.slideshowButton.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        }
    }

    private fun ensureSlideshowStopped() {
        if (_slideshowExecutor != null) {
            toggleSlideshow(binding.root)
        }
    }

    private fun layoutActivity() {
        displayView(binding.toolbar, _photoPrefs.doDisplayTopToolbar)
        displayView(binding.photoToolbar, _photoPrefs.doDisplayPhotoToolbar)
        displayView(binding.thumbnailPhotoRecycler, _photoPrefs.doDisplayThumbnails)

        if (_photoPrefs.doDisplayTopToolbar) {
            updateToolbar(binding.toolbar, _name.toString())
        }

        if (_photoPrefs.doDisplayThumbnails) {
            val llm =
                ThumbnailLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false, _index)
            binding.thumbnailPhotoRecycler.setHasFixedSize(true)
            binding.thumbnailPhotoRecycler.layoutManager = llm
            binding.thumbnailPhotoRecycler.adapter = _thumbnailRecyclerAdapter
            _disposables.add(
                _thumbnailRecyclerAdapter.onThumbnailSelected()
                    .subscribe { index: Int -> gotoPhoto(index) })

            if (_photoPrefs.doFadeControls) {
                binding.thumbnailPhotoRecycler.setOnTouchListener { view: View?, event: MotionEvent? ->
                    fade(binding.thumbnailPhotoRecycler)
                    false
                }
            }
        }

        if (!_photoPrefs.doFadeControls) {
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
            if (binding.toolbar.isShown) {
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
            if (binding.photoToolbar.isShown) {
                set.connect(
                    R.id.photoPager,
                    ConstraintSet.BOTTOM,
                    R.id.photoToolbar,
                    ConstraintSet.TOP,
                    0
                )
            } else if (binding.thumbnailPhotoRecycler.isShown) {
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
            set.applyTo(binding.container)
        }

        updateOpacity()
    }

    private fun displayView(view: View?, doShow: Boolean) {
        val visibility = if (doShow) View.VISIBLE else View.GONE
        view!!.visibility = visibility
    }

    private fun updateOpacity() {
        if (_photoPrefs.doFadeControls) {
            fade(binding.toolbar)
            fade(binding.photoToolbar)
            fade(binding.thumbnailPhotoRecycler)
        } else {
            appear(binding.toolbar)
            appear(binding.photoToolbar)
            appear(binding.thumbnailPhotoRecycler)
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
            val contentUri = _dataServices.getSharingContentUri(photo.imageMd.url)
            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.setDataAndType(contentUri, "image/*")
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

            return shareIntent
        }

        return null
    }

    private inner class SlideshowRunnable(private val _nextIndex: Int) :
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