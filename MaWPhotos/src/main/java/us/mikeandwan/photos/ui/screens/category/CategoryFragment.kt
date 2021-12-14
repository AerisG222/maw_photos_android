package us.mikeandwan.photos.ui.screens.category

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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentCategoryBinding

@AndroidEntryPoint
class CategoryFragment : Fragment() {
    companion object {
        fun newInstance() = CategoryFragment()
    }

    private lateinit var binding: FragmentCategoryBinding
    val viewModel by viewModels<CategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initStateObservers()

        return binding.root
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

        val action = CategoryFragmentDirections.actionNavigationCategoryToNavigationPhoto(id)

        findNavController().navigate(action)
    }
}