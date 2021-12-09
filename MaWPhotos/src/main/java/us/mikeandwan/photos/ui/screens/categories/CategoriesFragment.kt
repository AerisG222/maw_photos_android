package us.mikeandwan.photos.ui.screens.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentCategoriesBinding

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CategoriesFragment : Fragment() {
    companion object {
        fun newInstance() = CategoriesFragment()
    }

    private lateinit var binding: FragmentCategoriesBinding
    private val viewModel by viewModels<CategoriesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initStateObservers()

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.requestNavigateToCategory
                    .filter { it != null }
                    .onEach { navigateToCategory(it!!) }
                    .launchIn(this)
            }
        }
    }

    private fun navigateToCategory(categoryId: Int) {
        viewModel.onNavigateComplete()

        val action = CategoriesFragmentDirections.actionNavigationCategoriesToNavigationCategory(categoryId)

        findNavController().navigate(action)
    }
}