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
//                viewModel.refreshStatus
//                    .onEach {
//                        binding.container.isRefreshing = it.isRefreshing
//                    }
//                    .launchIn(this)
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
        viewModel.setThumbnailSize(thumbnailSize)
    }

    fun setGridItems(data: List<ImageGridItem>) {
        viewModel.setGridItems(data)
    }
}