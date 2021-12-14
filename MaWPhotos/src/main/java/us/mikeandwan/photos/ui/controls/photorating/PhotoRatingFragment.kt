package us.mikeandwan.photos.ui.controls.photorating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentPhotoRatingBinding
import kotlin.math.roundToInt

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@AndroidEntryPoint
class PhotoRatingFragment : Fragment() {
    companion object {
        fun newInstance() = PhotoRatingFragment()
    }

    private lateinit var binding: FragmentPhotoRatingBinding
    val viewModel by viewModels<PhotoRatingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoRatingBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.yourRatingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _ratingBar: RatingBar?, rating: Float, fromUser: Boolean ->
            if(fromUser) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.setRating(rating.roundToInt().toShort())
                    }
                }
            }
        }

        return binding.root
    }
}