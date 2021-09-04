package us.mikeandwan.photos.ui.photos

import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.ui.photos.BasePhotoDialogFragment
import io.reactivex.disposables.CompositeDisposable
import butterknife.Unbinder
import butterknife.BindView
import us.mikeandwan.photos.R
import android.widget.RatingBar
import javax.inject.Inject
import us.mikeandwan.photos.services.DataServices
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import android.widget.RatingBar.OnRatingBarChangeListener
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import android.view.WindowManager
import us.mikeandwan.photos.models.Rating

@AndroidEntryPoint
class RatingDialogFragment : BasePhotoDialogFragment() {
    private val _disposables = CompositeDisposable()
    private var _unbinder: Unbinder? = null

    @JvmField
    @BindView(R.id.yourRatingBar)
    var _yourRatingBar: RatingBar? = null

    @JvmField
    @BindView(R.id.averageRatingBar)
    var _averageRatingBar: RatingBar? = null

    @Inject lateinit var _dataServices: DataServices

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle
    ): View? {
        val view = inflater.inflate(R.layout.dialog_rating, container, false)
        _unbinder = ButterKnife.bind(this, view)
        _yourRatingBar!!.onRatingBarChangeListener =
            OnRatingBarChangeListener { _ratingBar: RatingBar?, rating: Float, fromUser: Boolean ->
                if (fromUser) {
                    _disposables.add(Flowable.fromCallable {
                        addWork()
                        _dataServices!!.setRating(currentPhoto.id, Math.round(rating))
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { x: Rating? ->
                                removeWork()
                                displayRating(x)
                            }
                        ) { ex: Throwable? ->
                            removeWork()
                            photoActivity.onApiException(ex)
                        }
                    )
                }
            }
        dialog.setTitle("Ratings")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        // http://stackoverflow.com/a/24213921
        val params = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = params
        ratings
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _disposables.clear() // do not send event after activity has been destroyed
        _unbinder!!.unbind()
    }

    private val ratings: Unit
        private get() {
            _yourRatingBar!!.rating = 0f
            _averageRatingBar!!.rating = 0f
            _disposables.add(Flowable.fromCallable {
                addWork()
                _dataServices!!.getRating(currentPhoto.id)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { x: Rating? ->
                        removeWork()
                        displayRating(x)
                    }
                ) { ex: Throwable? ->
                    removeWork()
                    photoActivity.onApiException(ex)
                }
            )
        }

    private fun displayRating(rating: Rating?) {
        if (rating == null) {
            _yourRatingBar!!.rating = 0f
            _averageRatingBar!!.rating = 0f
        } else {
            _yourRatingBar!!.rating = rating.userRating.toFloat()
            _averageRatingBar!!.rating = rating.averageRating
        }
    }
}