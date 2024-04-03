package us.mikeandwan.photos.ui.screens.random

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
import us.mikeandwan.photos.ui.theme.AppTheme

@AndroidEntryPoint
class RandomFragment : Fragment() {
    val viewModel by viewModels<RandomViewModel>()

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
                    RandomScreen(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        viewModel.onResume()

        super.onResume()
    }

    override fun onPause() {
        viewModel.onPause()

        super.onPause()
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.requestNavigateToPhoto
                    .filter { it != null }
                    .onEach { navigateToPhoto(it!!) }
                    .launchIn(this)
            }
        }
    }

    private fun navigateToPhoto(id: Int) {
        viewModel.onNavigateComplete()

        val action = RandomFragmentDirections.actionNavigationRandomToNavigationPhoto(id)

        findNavController().navigate(action)
    }
}