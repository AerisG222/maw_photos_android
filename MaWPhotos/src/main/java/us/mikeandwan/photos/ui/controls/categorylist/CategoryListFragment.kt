package us.mikeandwan.photos.ui.controls.categorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
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
import us.mikeandwan.photos.ui.CategoryClickListener
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridFragment
import us.mikeandwan.photos.ui.controls.yearnavmenu.YearListMenu

@AndroidEntryPoint
class CategoryListFragment : Fragment() {
    private var _clickHandler: CategoryClickListener? = null
    private var _refreshHandler: OnRefreshListener? = null
    val viewModel by viewModels<CategoryListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CategoryList(viewModel, _clickHandler)
            }
        }
    }

//    private fun initStateObservers() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.refreshStatus
//                    .onEach {
//                        binding.container.isRefreshing = it.isRefreshing
//                    }
//                    .launchIn(this)
//            }
//        }
//    }

    fun setRefreshStatus(refreshStatus: CategoryRefreshStatus) {
        viewModel.setRefreshStatus(refreshStatus)
    }

    fun setRefreshHandler(handler: OnRefreshListener?) {
        _refreshHandler = handler

//        binding.container.isEnabled = handler != null
    }

    fun setClickHandler(handler: CategoryClickListener) {
        _clickHandler = handler
    }

    fun setCategories(categories: List<PhotoCategory>) {
        viewModel.setCategories(categories)
    }

    fun setShowYear(doShow: Boolean) {
        viewModel.setShowYear(doShow)
    }
}