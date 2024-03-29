package us.mikeandwan.photos.ui.controls.categorychooser

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentCategoryChooserBinding
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.CategoryRefreshStatus
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryListFragment
import us.mikeandwan.photos.ui.controls.categorylist.CategoryListRecyclerAdapter
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridFragment
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridRecyclerAdapter
import us.mikeandwan.photos.ui.toImageGridItem

@AndroidEntryPoint
class CategoryChooserFragment: Fragment() {
    companion object {
        fun newInstance() = CategoryChooserFragment()

        const val FRAG_GRID = "grid"
        const val FRAG_LIST = "list"
    }

    private var _clickHandler: CategorySelectedListener? = null
    private var _refreshHandler: RefreshCategoriesListener? = null
    private lateinit var binding: FragmentCategoryChooserBinding
    val viewModel by viewModels<CategoryChooserViewModel>()

    private val onGridItemClicked = ImageGridRecyclerAdapter.ClickListener {
        onSelectCategory(it.data as PhotoCategory)
    }

    private val onListItemClicked = CategoryListRecyclerAdapter.ClickListener {
        onSelectCategory(it)
    }

    private val onRefreshCategories = OnRefreshListener {
        _refreshHandler?.onRefresh()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryChooserBinding.inflate(inflater)
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

    fun setDisplayType(type: CategoryDisplayType) {
        viewModel.setDisplayType(type)
    }

    fun setCategories(categories: List<PhotoCategory>) {
        viewModel.setCategories(categories)
    }

    fun setClickHandler(handler: CategorySelectedListener) {
        _clickHandler = handler
    }

    fun setRefreshHandler(handler: RefreshCategoriesListener) {
        _refreshHandler = handler
    }

    fun setRefreshStatus(status: CategoryRefreshStatus) {
        viewModel.setRefreshStatus(status)
    }

    fun setEnableRefresh(enable: Boolean) {
        viewModel.setEnableRefresh(enable)
    }

    fun setGridThumbnailSize(size: GridThumbnailSize) {
        viewModel.setGridThumbnailSize(size)
    }

    fun setShowYearsInList(doShow: Boolean) {
        viewModel.setShowYearsInList(doShow)
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.displayInfo
                    .onEach { info ->
                        when(info.displayType) {
                            CategoryDisplayType.Grid -> showGrid(info.gridThumbnailSize, info.categories, info.enableRefresh)
                            CategoryDisplayType.List -> showList(info.showYearInList, info.categories, info.enableRefresh)
                            CategoryDisplayType.Unspecified -> {}
                        }
                    }
                    .launchIn(this)

                viewModel.refreshStatus
                    .onEach {
                        if(!childFragmentManager.fragments.isEmpty()) {
                            when(val frag = childFragmentManager.fragments.first()) {
                                is ImageGridFragment -> frag.setRefreshStatus(it)
                                is CategoryListFragment -> frag.setRefreshStatus(it)
                            }
                        }
                    }
                    .launchIn(this)
            }
        }
    }

    private fun showGrid(thumbnailSize: GridThumbnailSize, categories: List<PhotoCategory>, enableRefresh: Boolean) {
        setChildFragment(ImageGridFragment::class.java, FRAG_GRID)

        val frag = childFragmentManager.fragments.first() as ImageGridFragment

        frag.setThumbnailSize(thumbnailSize)
        frag.setClickHandler(onGridItemClicked)
        frag.setGridItems(categories.map{ it.toImageGridItem() })

        if(enableRefresh) {
            frag.setRefreshHandler(onRefreshCategories)
        } else {
            frag.setRefreshHandler(null)
        }
    }

    private fun showList(showYearInList: Boolean, categories: List<PhotoCategory>, enableRefresh: Boolean) {
        setChildFragment(CategoryListFragment::class.java, FRAG_LIST)

        val frag = childFragmentManager.fragments.first() as CategoryListFragment

        frag.setShowYear(showYearInList)
        frag.setClickHandler(onListItemClicked)
        frag.setCategories(categories)

        if(enableRefresh) {
            frag.setRefreshHandler(onRefreshCategories)
        } else {
            frag.setRefreshHandler(null)
        }
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

            add(R.id.categoryListOrGridFragment, fragmentClass, null, tag)
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
        _clickHandler?.onClick(category)
    }

    class CategorySelectedListener(val clickListener: (item: PhotoCategory) -> Unit) {
        fun onClick(item: PhotoCategory) = clickListener(item)
    }

    class RefreshCategoriesListener(val refreshListener: () -> Unit) {
        fun onRefresh() = refreshListener()
    }
}