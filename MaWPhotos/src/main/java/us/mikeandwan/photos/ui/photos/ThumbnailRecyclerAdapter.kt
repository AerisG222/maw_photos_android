package us.mikeandwan.photos.ui.photos

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import us.mikeandwan.photos.R
import us.mikeandwan.photos.models.Photo
import us.mikeandwan.photos.models.PhotoSize
import us.mikeandwan.photos.services.DataServices

class ThumbnailRecyclerAdapter(activity: IPhotoActivity, dataServices: DataServices) :
    RecyclerView.Adapter<ThumbnailRecyclerAdapter.ViewHolder>() {
    private val _disposables = CompositeDisposable()
    private val _context: Context
    private val _activity: IPhotoActivity
    private val _dataServices: DataServices
    private val _thumbnailSubject = PublishSubject.create<Int>()
    private var _photoList: List<Photo?>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = ImageView(context)
        view.scaleType = ImageView.ScaleType.CENTER
        view.setPadding(2, 0, 2, 0)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val photo = _photoList!![position]
        viewHolder.itemView.setOnClickListener { v: View? -> _thumbnailSubject.onNext(position) }
        _disposables.add(Flowable.fromCallable {
            _activity.addWork()
            _dataServices.downloadPhoto(photo, PhotoSize.Xs)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: String ->
                    _activity.removeWork()
                    displayPhoto(viewHolder, x)
                }
            ) { ex: Throwable? ->
                _activity.removeWork()
                _activity.onApiException(ex)
            }
        )
    }

    override fun getItemCount(): Int {
        return _photoList!!.size
    }

    fun refreshPhotoList() {
        _photoList = _activity.photoList
        notifyDataSetChanged()
    }

    fun onThumbnailSelected(): Observable<Int> {
        return _thumbnailSubject.hide()
    }

    fun dispose() {
        _disposables.dispose()
    }

    private fun displayPhoto(viewHolder: ViewHolder, path: String) {
        Picasso
            .get()
            .load(path)
            .resizeDimen(R.dimen.photo_list_thumbnail_size, R.dimen.photo_list_thumbnail_size)
            .centerCrop()
            .into(viewHolder._thumbnailImageView)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var _thumbnailImageView: ImageView?

        init {
            _thumbnailImageView = itemView as ImageView?
        }
    }

    init {
        _context = activity as Context
        _activity = activity
        _dataServices = dataServices
    }
}