package us.mikeandwan.photos.uinew.ui.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentCategoriesBinding
import us.mikeandwan.photos.domain.CategoryDisplayType
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridFragment
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridItem
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridRecyclerAdapter

@AndroidEntryPoint
class CategoriesFragment : Fragment() {
    companion object {
        fun newInstance() = CategoriesFragment()

        const val FRAG_GRID = "grid"
        const val FRAG_LIST = "list"
    }

    private lateinit var binding: FragmentCategoriesBinding
    private val viewModel by viewModels<CategoriesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        if(savedInstanceState == null) {
            initStateObservers()
        } else {
            val frag = childFragmentManager.fragments.first() as ImageGridFragment

            frag.setClickHandler(onCategoryClicked)
        }

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.displayType
                    .combine(viewModel.categories) { type, categories -> Pair(type, categories) }
                    .drop(1)
                    .onEach { (displayType, categories) ->
                        showGrid()
                        delay(1)   // TODO: find a way to get rid of this
                        updateCategories(categories)
                    }
                    .launchIn(this)
            }
        }
    }

    private val onCategoryClicked = ImageGridRecyclerAdapter.ClickListener {
        viewModel.onCategorySelected(it.id)
        navigateToCategory(it.id)
    }

    private fun updateCategories(categories: List<ImageGridItem>) {
        when(val frag = childFragmentManager.fragments.first()) {
            is ImageGridFragment -> {
                frag.setClickHandler(onCategoryClicked)
                frag.setData(categories)
            }
        }
    }

    private fun showGrid() {
        setChildFragment(R.layout.fragment_image_grid, ImageGridFragment::class.java, FRAG_GRID)
    }

    private fun <T: Fragment> setChildFragment(id: Int, fragmentClass: Class<T>, tag: String) {
        val fragment = getCurrentFragment()
        var doRemove = false

        if(fragment != null) {
            if(fragment.tag == tag) {
                return
            } else {
                doRemove = true
            }
        }

        childFragmentManager.commit {
            setReorderingAllowed(true)

            if(doRemove) {
                remove(fragment!!)
            }

            add(R.id.fragmentCategoryList, fragmentClass, null, tag)
        }

        childFragmentManager.executePendingTransactions()
    }

    private fun getCurrentFragment(): Fragment? {
        return if(childFragmentManager.fragments.isNotEmpty()) {
            childFragmentManager.fragments.first()
        } else {
            null
        }
    }

    /*
    private fun showList() {
        if(updateListAdapterRequired()) {
            val decoration = FlexboxItemDecoration(binding.categoryRecyclerView.context)

            decoration.setOrientation(FlexboxItemDecoration.HORIZONTAL)

            updateAdapter(CategoryListRecyclerAdapter(onCategoryClicked), decoration)
        }
    }

    private fun clearRecyclerDecorations() {
        for(i in binding.categoryRecyclerView.itemDecorationCount - 1 downTo 0) {
            binding.categoryRecyclerView.removeItemDecorationAt(i)
        }
    }

    private fun updateGridAdapterRequired() =
        binding.categoryRecyclerView.adapter !is CategoryGridRecyclerAdapter || _width.value != 0

    private fun updateListAdapterRequired() =
        binding.categoryRecyclerView.adapter !is CategoryListRecyclerAdapter
     */

    private fun navigateToCategory(categoryId: Int) {
        val action = CategoriesFragmentDirections.actionNavigationCategoriesToNavigationPhotos(categoryId)

        findNavController().navigate(action)
    }
}