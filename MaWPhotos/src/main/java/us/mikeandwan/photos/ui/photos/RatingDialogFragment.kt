package us.mikeandwan.photos.ui.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import us.mikeandwan.photos.databinding.DialogRatingBinding
import us.mikeandwan.photos.models.Rating
import us.mikeandwan.photos.services.DataServices
import javax.inject.Inject

@AndroidEntryPoint
class RatingDialogFragment : BasePhotoDialogFragment() {
    private val _disposables = CompositeDisposable()

    private var _binding: DialogRatingBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var _dataServices: DataServices

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogRatingBinding.inflate(inflater, container, false)

        binding.yourRatingBar.onRatingBarChangeListener =
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

        requireDialog().setTitle("Ratings")

        return binding.root
    }

    override fun onResume() {
        // http://stackoverflow.com/a/24213921
        val params = requireDialog().window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        requireDialog().window!!.attributes = params
        ratings
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _disposables.clear() // do not send event after activity has been destroyed
    }

    private val ratings: Unit
        private get() {
            binding.yourRatingBar.rating = 0f
            binding.averageRatingBar.rating = 0f
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
            binding.yourRatingBar.rating = 0f
            binding.averageRatingBar.rating = 0f
        } else {
            binding.yourRatingBar.rating = rating.userRating.toFloat()
            binding.averageRatingBar.rating = rating.averageRating
        }
    }
}