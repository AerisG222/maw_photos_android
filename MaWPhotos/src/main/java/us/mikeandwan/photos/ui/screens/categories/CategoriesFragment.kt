package us.mikeandwan.photos.ui.screens.categories

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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.ui.theme.AppTheme

@AndroidEntryPoint
class CategoriesFragment : Fragment() {
    private val viewModel by viewModels<CategoriesViewModel>()

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
                    CategoriesScreen(viewModel = viewModel)
                }
            }
        }
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.requestNavigateToCategory
                    .filter { it != null }
                    .onEach { navigateToCategory(it!!) }
                    .launchIn(this)

//                viewModel.refreshStatus
//                    .onEach {
//                        if(it.message != null) {
//                            val snackbar = Snackbar.make(binding.container.context, binding.root, it.message, Snackbar.LENGTH_SHORT)
//
//                            snackbar.show()
//                        }
//                    }
//                    .launchIn(this)
            }
        }
    }

    private fun navigateToCategory(categoryId: Int) {
        viewModel.onNavigateComplete()

        val action = CategoriesFragmentDirections.actionNavigationCategoriesToNavigationCategory(categoryId)

        findNavController().navigate(action)
    }
}
