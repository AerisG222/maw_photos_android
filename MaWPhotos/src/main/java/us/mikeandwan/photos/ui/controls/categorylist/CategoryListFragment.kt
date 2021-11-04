package us.mikeandwan.photos.ui.controls.categorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentCategoryListBinding
import us.mikeandwan.photos.domain.PhotoCategory
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridFragment

@AndroidEntryPoint
class CategoryListFragment : Fragment() {
    companion object {
        fun newInstance() = ImageGridFragment()
    }

    private val _clickHandlerForwarder = CategoryListRecyclerAdapter.ClickListener { _clickHandler?.onClick(it) }
    private var _clickHandler: CategoryListRecyclerAdapter.ClickListener? = null
    private lateinit var binding: FragmentCategoryListBinding
    val viewModel by viewModels<CategoryListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryListBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.categoryListRecyclerView.setHasFixedSize(true)
        binding.categoryListRecyclerView.adapter = CategoryListRecyclerAdapter(_clickHandlerForwarder)

        val decoration = DividerItemDecoration(binding.categoryListRecyclerView.context, DividerItemDecoration.VERTICAL)

        binding.categoryListRecyclerView.addItemDecoration(decoration)

        initStateObservers()

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.doInvalidate
                    .onEach {
                        delay(1)
                        if(it) {
                            viewModel.setDoInvalidate(false)

                            val adapter = binding.categoryListRecyclerView.adapter as CategoryListRecyclerAdapter
                            adapter.submitList(viewModel.categories.value)
                        }
                    }
                    .launchIn(this)
            }
        }
    }

    fun setClickHandler(handler: CategoryListRecyclerAdapter.ClickListener) {
        _clickHandler = handler
    }

    fun setData(categories: List<PhotoCategory>) {
        viewModel.setCategories(categories)
    }
}