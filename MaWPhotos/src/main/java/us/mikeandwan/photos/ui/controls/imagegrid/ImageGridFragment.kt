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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.flexbox.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentImageGridBinding
import us.mikeandwan.photos.domain.models.CategoryRefreshStatus
import us.mikeandwan.photos.domain.models.GridThumbnailSize

@AndroidEntryPoint
class ImageGridFragment : Fragment() {
    companion object {
        fun newInstance() = ImageGridFragment()
    }

    private val _screenWidth = MutableStateFlow(-1)
    private val _clickHandlerForwarder = ImageGridRecyclerAdapter.ClickListener { _clickHandler?.onClick(it) }
    private var _clickHandler: ImageGridRecyclerAdapter.ClickListener? = null
    private val _refreshHandlerForwarder = SwipeRefreshLayout.OnRefreshListener { _refreshHandler?.onRefresh() }
    private var _refreshHandler: SwipeRefreshLayout.OnRefreshListener? = null;
    private var _listener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private lateinit var binding: FragmentImageGridBinding
    val viewModel by viewModels<ImageGridViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageGridBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.imageGridRecyclerView.setHasFixedSize(true)

        // TODO: i believe space_between should push the start and end images against the edges, but this is not working
        // if i wrap the ImageView in the item layout in a LinearLayout, then the first image will be flush on the left
        // but the right image has a full padding of space at the end.  If I remove the wrapping layout, then the padding
        // is evenly spaced on both left and right, which is more visually appealing
        binding.imageGridRecyclerView.layoutManager = FlexboxLayoutManager(activity).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.FLEX_START
            justifyContent = JustifyContent.SPACE_BETWEEN
        }

        val adapter = ImageGridRecyclerAdapter(_clickHandlerForwarder)

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.imageGridRecyclerView.adapter = adapter

        binding.container.isEnabled = false
        binding.container.setOnRefreshListener(_refreshHandlerForwarder)

        initStateObservers()
        listenForWidth()

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    _screenWidth,
                    viewModel.requestedThumbnailSize
                ) { screenWidth, thumbnailSize -> Pair(screenWidth, thumbnailSize) }
                .filter { (screenWidth, thumbnailSize) -> screenWidth > 0 && thumbnailSize != GridThumbnailSize.Unspecified }
                .onEach { (screenWidth, thumbnailSize) -> viewModel.setThumbnailSize(getThumbnailSize(screenWidth, thumbnailSize)) }
                .launchIn(this)

                viewModel.refreshStatus
                    .onEach {
                        binding.container.isRefreshing = it.isRefreshing
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

    fun setRefreshStatus(refreshStatus: CategoryRefreshStatus) {
        viewModel.setRefreshStatus(refreshStatus)
    }

    fun setRefreshHandler(handler: SwipeRefreshLayout.OnRefreshListener) {
        _refreshHandler = handler

        binding.container.isEnabled = true
    }

    fun setClickHandler(handler: ImageGridRecyclerAdapter.ClickListener) {
        _clickHandler = handler
    }

    fun setThumbnailSize(thumbnailSize: GridThumbnailSize) {
        viewModel.setRequestedThumbnailSize(thumbnailSize)
    }

    fun setGridItems(data: List<ImageGridItem>) {
        viewModel.setGridItems(data)
    }

    private fun getThumbnailSize(screenWidth: Int, thumbnailSize: GridThumbnailSize): Int {
        if(thumbnailSize == GridThumbnailSize.Unspecified) {
            return 0
        }

        val thumbSize = getThumbnailSizeInDps(thumbnailSize)
        val cols = maxOf(1, screenWidth / thumbSize)
        val totalInteriorMargins = (cols - 1) * resources.getDimension(R.dimen._2dp)
        val remainingSpaceForImages = screenWidth - totalInteriorMargins

        return (remainingSpaceForImages / cols).toInt()
    }

    private fun getThumbnailSizeInDps(thumbnailSize: GridThumbnailSize): Int {
        return when(thumbnailSize) {
            GridThumbnailSize.ExtraSmall -> resources.getDimension(R.dimen.image_grid_thumbnail_size_extra_small).toInt()
            GridThumbnailSize.Small -> resources.getDimension(R.dimen.image_grid_thumbnail_size_small).toInt()
            GridThumbnailSize.Medium -> resources.getDimension(R.dimen.image_grid_thumbnail_size_medium).toInt()
            GridThumbnailSize.Large -> resources.getDimension(R.dimen.image_grid_thumbnail_size_large).toInt()
            GridThumbnailSize.Unspecified -> 0
        }
    }
}