package us.mikeandwan.photos.ui.photos

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import us.mikeandwan.photos.models.Photo
import us.mikeandwan.photos.models.PhotoSize
import us.mikeandwan.photos.services.DataServices

// http://stackoverflow.com/questions/11306037/how-to-implement-zoom-pan-and-drag-on-viewpager-in-android
class FullScreenImageAdapter(activity: IPhotoActivity, dataServices: DataServices) :
    PagerAdapter() {
    private val _disposables = CompositeDisposable()
    private val _context: Context
    private val _activity: IPhotoActivity
    private val _dataServices: DataServices
    private var _photoList: List<Photo>? = null
    override fun getCount(): Int {
        return _photoList!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = PhotoView(container.context)
        photoView.tag = position
        displayImage(photoView, _photoList!![position])
        container.addView(photoView)
        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as PhotoView)
    }

    fun dispose() {
        _disposables.dispose()
    }

    fun refreshPhotoList() {
        _photoList = _activity.photoList
        notifyDataSetChanged()
    }

    private fun displayImage(view: PhotoView, photo: Photo) {
        _disposables.add(Flowable.fromCallable {
            _activity.addWork()
            _dataServices.downloadPhoto(photo, PhotoSize.Md)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: String? ->
                    _activity.removeWork()
                    Picasso
                        .get()
                        .load(x)
                        .into(view)
                }
            ) { ex: Throwable? ->
                _activity.removeWork()
                _activity.onApiException(ex)
            }
        )
    }

    init {
        _context = activity as Context
        _activity = activity
        _dataServices = dataServices
    }
}