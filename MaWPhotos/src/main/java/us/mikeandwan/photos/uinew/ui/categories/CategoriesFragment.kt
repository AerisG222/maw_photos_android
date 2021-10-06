package us.mikeandwan.photos.uinew.ui.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.databinding.FragmentCategoriesBinding
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

        binding.categoryRecyclerView.adapter = CategoryListRecyclerAdapter(CategoryListRecyclerAdapter.ClickListener {
            viewModel.onCategorySelected(it)
            navigateToCategory(it)
        })

        return binding.root
    }

    private fun navigateToCategory(category: PhotoCategory) {
        val action = CategoriesFragmentDirections.actionNavigationCategoriesToNavigationPhotos(category.id)

        findNavController().navigate(action)
    }
}