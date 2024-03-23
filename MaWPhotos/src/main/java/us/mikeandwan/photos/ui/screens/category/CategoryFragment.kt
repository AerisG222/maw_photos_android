package us.mikeandwan.photos.ui.screens.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridScreen
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridViewModel
import us.mikeandwan.photos.ui.theme.AppTheme
import us.mikeandwan.photos.ui.toImageGridItem

@AndroidEntryPoint
class CategoryFragment : Fragment() {
    val viewModel by viewModels<CategoryViewModel>()
    private val imageGridViewModel = ImageGridViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initStateObservers()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    ImageGridScreen(imageGridViewModel, viewModel.onPhotoClicked)
                }
            }
        }
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photos
                    .onEach { photos -> imageGridViewModel.setGridItems(photos.map { photo -> photo.toImageGridItem() }) }
                    .launchIn(this)

                viewModel.gridItemThumbnailSize
                    .onEach { size -> imageGridViewModel.setThumbnailSize(size) }
                    .launchIn(this)

                viewModel.requestNavigateToPhoto
                    .filter { it != null }
                    .onEach { navigateToPhoto(it!!) }
                    .launchIn(this)
            }
        }
    }

    private fun navigateToPhoto(id: Int) {
        viewModel.onNavigateComplete()

        val action = CategoryFragmentDirections.actionNavigationCategoryToNavigationPhoto(id)

        findNavController().navigate(action)
    }
}