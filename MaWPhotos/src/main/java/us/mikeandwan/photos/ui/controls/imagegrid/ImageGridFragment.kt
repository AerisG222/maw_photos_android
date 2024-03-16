package us.mikeandwan.photos.ui.controls.imagegrid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.CategoryRefreshStatus
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.ui.ImageGridClickListener

@AndroidEntryPoint
class ImageGridFragment : Fragment() {
    private var _clickHandler: ImageGridClickListener? = null
    private var _refreshHandler: SwipeRefreshLayout.OnRefreshListener? = null
    val viewModel by viewModels<ImageGridViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ImageGrid(viewModel, _clickHandler)
            }
        }
    }

//    private fun initStateObservers() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                combine(
//                    _screenWidth,
//                    viewModel.requestedThumbnailSize
//                ) { screenWidth, thumbnailSize -> Pair(screenWidth, thumbnailSize) }
//                .filter { (screenWidth, thumbnailSize) -> screenWidth > 0 && thumbnailSize != GridThumbnailSize.Unspecified }
//                .onEach { (screenWidth, thumbnailSize) -> viewModel.setThumbnailSize(getThumbnailSize(screenWidth, thumbnailSize)) }
//                .launchIn(this)
//
//                viewModel.refreshStatus
//                    .onEach {
//                        binding.container.isRefreshing = it.isRefreshing
//                    }
//                    .launchIn(this)
//
//            }
//        }
//    }

    fun setRefreshStatus(refreshStatus: CategoryRefreshStatus) {
        viewModel.setRefreshStatus(refreshStatus)
    }

    fun setRefreshHandler(handler: SwipeRefreshLayout.OnRefreshListener?) {
        _refreshHandler = handler

//        binding.container.isEnabled = handler != null
    }

    fun setClickHandler(handler: ImageGridClickListener) {
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