package us.mikeandwan.photos.ui.screens.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentCategoriesBinding
import us.mikeandwan.photos.domain.CategoryDisplayType
import us.mikeandwan.photos.domain.GridThumbnailSize
import us.mikeandwan.photos.domain.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryListFragment
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridFragment
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridRecyclerAdapter
import us.mikeandwan.photos.ui.toImageGridItem

@AndroidEntryPoint
class CategoriesFragment : Fragment() {
    companion object {
        fun newInstance() = CategoriesFragment()

        const val FRAG_GRID = "grid"
        const val FRAG_LIST = "list"
    }

    private lateinit var binding: FragmentCategoriesBinding
    private val viewModel by viewModels<CategoriesViewModel>()

    private val onGridItemClicked = ImageGridRecyclerAdapter.ClickListener {
        onSelectCategory(it.data as PhotoCategory)
    }

    private val onListItemClicked = us.mikeandwan.photos.ui.controls.categorylist.CategoryListRecyclerAdapter.ClickListener {
        onSelectCategory(it)
    }

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
            when(val frag = childFragmentManager.fragments.first()) {
                is ImageGridFragment -> frag.setClickHandler(onGridItemClicked)
                is CategoryListFragment -> frag.setClickHandler(onListItemClicked)
            }
        }

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.preferences
                    .combine(viewModel.categories) { preferences, categories -> Pair(preferences, categories) }
                    .onEach { (preferences, categories) ->
                        when(preferences.displayType) {
                            CategoryDisplayType.Grid -> showGrid(preferences.gridThumbnailSize)
                            CategoryDisplayType.List -> showList()
                        }

                        delay(1)   // TODO: find a way to get rid of this

                        updateCategories(categories)
                    }
                    .launchIn(this)
            }
        }
    }

    private fun updateCategories(categories: List<PhotoCategory>) {
        when(val frag = childFragmentManager.fragments.first()) {
            is ImageGridFragment -> {
                frag.setClickHandler(onGridItemClicked)
                frag.setData(categories.map{ it.toImageGridItem() })
            }
            is CategoryListFragment -> {
                frag.setClickHandler(onListItemClicked)
                frag.setData(categories)
            }
        }
    }

    private fun showGrid(thumbnailSize: GridThumbnailSize) {
        setChildFragment(R.layout.fragment_image_grid, ImageGridFragment::class.java, FRAG_GRID)

        val frag = childFragmentManager.fragments.first() as ImageGridFragment

        frag.setThumbnailSize(thumbnailSize)
    }

    private fun showList() {
        setChildFragment(R.layout.fragment_category_list, CategoryListFragment::class.java, FRAG_LIST)
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

    private fun onSelectCategory(category: PhotoCategory) {
        viewModel.onCategorySelected(category.id)
        navigateToCategory(category.id)
    }

    private fun navigateToCategory(categoryId: Int) {
        val action = CategoriesFragmentDirections.actionNavigationCategoriesToNavigationCategory(categoryId)

        findNavController().navigate(action)
    }
}