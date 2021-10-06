package us.mikeandwan.photos.uinew.ui.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentCategoriesBinding
import us.mikeandwan.photos.domain.CategoryDisplayType
import us.mikeandwan.photos.domain.PhotoCategory

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
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.categoryRecyclerView.layoutManager = FlexboxLayoutManager(activity).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }

        initStateObservers()

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.viewModel?.displayType?.collect { type ->
                    when(type) {
                        CategoryDisplayType.Grid -> showGrid()
                        CategoryDisplayType.List -> showList()
                    }
                }
            }
        }
    }

    private val onCategoryClicked = CategoryRecyclerAdapter.ClickListener {
        viewModel.onCategorySelected(it)
        navigateToCategory(it)
    }

    private fun showGrid() {
        if(isGridAdapterActive()) {
            return
        }

        updateAdapter(CategoryGridRecyclerAdapter(onCategoryClicked))
    }

    private fun showList() {
        if(isListAdapterActive()) {
            return
        }

        updateAdapter(CategoryListRecyclerAdapter(onCategoryClicked))
    }

    private fun <T : RecyclerView.ViewHolder?> updateAdapter(adapter: RecyclerView.Adapter<T>) {
        binding.categoryRecyclerView.adapter = adapter
        binding.invalidateAll()
    }

    private fun isGridAdapterActive() =
        binding.categoryRecyclerView.adapter is CategoryGridRecyclerAdapter

    private fun isListAdapterActive() =
        binding.categoryRecyclerView.adapter is CategoryListRecyclerAdapter

    private fun navigateToCategory(category: PhotoCategory) {
        val action = CategoriesFragmentDirections.actionNavigationCategoriesToNavigationPhotos(category.id)

        findNavController().navigate(action)
    }
}