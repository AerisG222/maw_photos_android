package us.mikeandwan.photos.uinew.ui.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentCategoriesBinding
import us.mikeandwan.photos.domain.CategoryDisplayType
import us.mikeandwan.photos.domain.PhotoCategory

@AndroidEntryPoint
class CategoriesFragment : Fragment() {
    companion object {
        fun newInstance() = CategoriesFragment()
    }

    private var _thumbSize = 0
    private var _listener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private val _width = MutableStateFlow<Int>(0)
    private lateinit var binding: FragmentCategoriesBinding
    private val viewModel by viewModels<CategoriesViewModel>()
    private val args: CategoriesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        _thumbSize = resources.getDimension(R.dimen.category_grid_thumbnail_size).toInt()

        Timber.i("active year: ${args.year}")

        initStateObservers()
        listenForWidth()

        binding.categoryRecyclerView.layoutManager = FlexboxLayoutManager(activity).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
            justifyContent = JustifyContent.SPACE_BETWEEN
        }

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                _width.collect { showGrid() }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.displayType.collect { type ->
                    when (type) {
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

    private fun listenForWidth() {
        _listener = ViewTreeObserver.OnGlobalLayoutListener {
            binding.container.viewTreeObserver.removeOnGlobalLayoutListener(_listener)
            _width.value = binding.container.width
        }

        binding.container.viewTreeObserver.addOnGlobalLayoutListener(_listener)
    }

    private fun showGrid() {
        if(updateGridAdapterRequired()) {
            updateAdapter(CategoryGridRecyclerAdapter(getThumbnailSize(), onCategoryClicked))
        }
    }

    private fun showList() {
        if(updateListAdapterRequired()) {
            updateAdapter(CategoryListRecyclerAdapter(onCategoryClicked))
        }
    }

    private fun <T : RecyclerView.ViewHolder?> updateAdapter(adapter: RecyclerView.Adapter<T>) {
        binding.categoryRecyclerView.adapter = adapter
        binding.invalidateAll()
    }

    private fun getThumbnailSize(): Int {
        val width = _width.value
        val cols = maxOf(1, width / _thumbSize)

        val totalInteriorMargins = cols * resources.getDimension(R.dimen._2dp)
        val remainingSpaceForImages = width - totalInteriorMargins.toInt()

        return remainingSpaceForImages / cols
    }

    private fun updateGridAdapterRequired() =
        binding.categoryRecyclerView.adapter !is CategoryGridRecyclerAdapter || _width.value != 0

    private fun updateListAdapterRequired() =
        binding.categoryRecyclerView.adapter !is CategoryListRecyclerAdapter

    private fun navigateToCategory(category: PhotoCategory) {
        val action = CategoriesFragmentDirections.actionNavigationCategoriesToNavigationPhotos(category.id)

        findNavController().navigate(action)
    }
}