package us.mikeandwan.photos.ui.controls.imagegrid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentImageGridBinding
import us.mikeandwan.photos.domain.models.GridThumbnailSize

@AndroidEntryPoint
class ImageGridFragment : Fragment() {
    companion object {
        fun newInstance() = ImageGridFragment()
    }

    private val _screenWidth = MutableStateFlow<Int>(-1)
    private val _clickHandlerForwarder = ImageGridRecyclerAdapter.ClickListener { _clickHandler?.onClick(it) }
    private var _clickHandler: ImageGridRecyclerAdapter.ClickListener? = null
    private var _listener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private lateinit var binding: FragmentImageGridBinding
    val viewModel by viewModels<ImageGridViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageGridBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.imageGridRecyclerView.setHasFixedSize(true)
        binding.imageGridRecyclerView.layoutManager = FlexboxLayoutManager(activity).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.FLEX_START
            justifyContent = JustifyContent.FLEX_START
        }

        val adapter = ImageGridRecyclerAdapter(_clickHandlerForwarder)

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.imageGridRecyclerView.adapter = adapter

        initStateObservers()
        listenForWidth()

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                _screenWidth.combine(viewModel.requestedThumbnailSize) { screenWidth, thumbnailSize ->
                    if(screenWidth > 0 && thumbnailSize != null) {
                        viewModel.setThumbnailSize(getThumbnailSize(screenWidth, thumbnailSize))
                    }
                }
                .launchIn(this)
            }
        }
    }

    private fun listenForWidth() {
        _listener = ViewTreeObserver.OnGlobalLayoutListener {
            binding.container.viewTreeObserver.removeOnGlobalLayoutListener(_listener)

            _screenWidth.value = binding.container.width
        }

        binding.container.viewTreeObserver.addOnGlobalLayoutListener(_listener)
    }

    fun setClickHandler(handler: ImageGridRecyclerAdapter.ClickListener) {
        _clickHandler = handler
    }

    fun setThumbnailSize(thumbnailSize: GridThumbnailSize) {
        viewModel.setRequestedThumbnailSize(thumbnailSize)
    }

    fun setData(data: List<ImageGridItem>) {
        viewModel.setData(data)
    }

    private fun getThumbnailSize(screenWidth: Int, thumbnailSize: GridThumbnailSize): Int {
        val thumbSize = getThumbnailSizeInDps(thumbnailSize)
        val cols = maxOf(1, screenWidth / thumbSize)

        val totalInteriorMargins = cols * resources.getDimension(R.dimen._2dp)
        val remainingSpaceForImages = screenWidth - totalInteriorMargins.toInt()

        return remainingSpaceForImages / cols
    }

    private fun getThumbnailSizeInDps(thumbnailSize: GridThumbnailSize): Int {
        return when(thumbnailSize) {
            GridThumbnailSize.ExtraSmall -> resources.getDimension(R.dimen.image_grid_thumbnail_size_extra_small).toInt()
            GridThumbnailSize.Small -> resources.getDimension(R.dimen.image_grid_thumbnail_size_small).toInt()
            GridThumbnailSize.Medium -> resources.getDimension(R.dimen.image_grid_thumbnail_size_medium).toInt()
            GridThumbnailSize.Large -> resources.getDimension(R.dimen.image_grid_thumbnail_size_large).toInt()
        }
    }
}