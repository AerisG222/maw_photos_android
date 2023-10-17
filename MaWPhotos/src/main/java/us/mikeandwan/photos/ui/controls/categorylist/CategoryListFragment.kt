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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentCategoryListBinding
import us.mikeandwan.photos.domain.models.CategoryRefreshStatus
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridFragment

@AndroidEntryPoint
class CategoryListFragment : Fragment() {
    companion object {
        fun newInstance() = ImageGridFragment()
    }

    private val _clickHandlerForwarder = CategoryListRecyclerAdapter.ClickListener { _clickHandler?.onClick(it) }
    private var _clickHandler: CategoryListRecyclerAdapter.ClickListener? = null
    private val _refreshHandlerForwarder = OnRefreshListener { _refreshHandler?.onRefresh() }
    private var _refreshHandler: OnRefreshListener? = null
    private lateinit var binding: FragmentCategoryListBinding
    val viewModel by viewModels<CategoryListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryListBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.categoryListRecyclerView.setHasFixedSize(true)

        val adapter = CategoryListRecyclerAdapter(_clickHandlerForwarder)

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.categoryListRecyclerView.adapter = adapter

        val decoration = DividerItemDecoration(binding.categoryListRecyclerView.context, DividerItemDecoration.VERTICAL)

        binding.categoryListRecyclerView.addItemDecoration(decoration)

        binding.container.isEnabled = false
        binding.container.setOnRefreshListener(_refreshHandlerForwarder)

        initStateObservers()

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.refreshStatus
                    .onEach {
                        binding.container.isRefreshing = it.isRefreshing
                    }
                    .launchIn(this)
            }
        }
    }

    fun setRefreshStatus(refreshStatus: CategoryRefreshStatus) {
        viewModel.setRefreshStatus(refreshStatus)
    }

    fun setRefreshHandler(handler: OnRefreshListener) {
        _refreshHandler = handler

        binding.container.isEnabled = true
    }

    fun setClickHandler(handler: CategoryListRecyclerAdapter.ClickListener) {
        _clickHandler = handler
    }

    fun setCategories(categories: List<PhotoCategory>) {
        viewModel.setCategories(categories)
    }

    fun setShowYear(doShow: Boolean) {
        viewModel.setShowYear(doShow)
    }
}