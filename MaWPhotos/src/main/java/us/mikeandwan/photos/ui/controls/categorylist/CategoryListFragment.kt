package us.mikeandwan.photos.ui.controls.categorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.databinding.FragmentCategoryListBinding
import us.mikeandwan.photos.domain.models.PhotoCategory
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

        val adapter = CategoryListRecyclerAdapter(_clickHandlerForwarder)

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.categoryListRecyclerView.adapter = adapter

        val decoration = DividerItemDecoration(binding.categoryListRecyclerView.context, DividerItemDecoration.VERTICAL)

        binding.categoryListRecyclerView.addItemDecoration(decoration)

        return binding.root
    }

    fun setClickHandler(handler: CategoryListRecyclerAdapter.ClickListener) {
        _clickHandler = handler
    }

    fun setCategories(categories: List<PhotoCategory>) {
        viewModel.setCategories(categories)
    }
}