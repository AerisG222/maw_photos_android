package us.mikeandwan.photos.ui.screens.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentCategoriesBinding
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryListFragment
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridFragment
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridRecyclerAdapter
import us.mikeandwan.photos.ui.toImageGridItem

@ExperimentalCoroutinesApi
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
    ): View {
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
                    .filter { (preferences, categories) -> preferences.gridThumbnailSize != GridThumbnailSize.Unspecified && categories.isNotEmpty() }
                    .onEach { (preferences, categories) ->
                        when(preferences.displayType) {
                            CategoryDisplayType.Grid -> showGrid(preferences.gridThumbnailSize, categories)
                            CategoryDisplayType.List -> showList(categories)
                        }
                    }
                    .launchIn(this)
            }
        }
    }

    private fun showGrid(thumbnailSize: GridThumbnailSize, categories: List<PhotoCategory>) {
        setChildFragment(ImageGridFragment::class.java, FRAG_GRID)

        val frag = childFragmentManager.fragments.first() as ImageGridFragment

        frag.setThumbnailSize(thumbnailSize)
        frag.setClickHandler(onGridItemClicked)
        frag.setGridItems(categories.map{ it.toImageGridItem() })
    }

    private fun showList(categories: List<PhotoCategory>) {
        setChildFragment(CategoryListFragment::class.java, FRAG_LIST)

        val frag = childFragmentManager.fragments.first() as CategoryListFragment

        frag.setClickHandler(onListItemClicked)
        frag.setCategories(categories)
    }

    private fun <T: Fragment> setChildFragment(fragmentClass: Class<T>, tag: String) {
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