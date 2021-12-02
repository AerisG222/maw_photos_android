package us.mikeandwan.photos.ui.controls.searchnavmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.databinding.FragmentSearchNavMenuBinding


@AndroidEntryPoint
class SearchNavMenuFragment : Fragment() {
    companion object {
        fun newInstance() = SearchNavMenuFragment()
    }

    private lateinit var binding: FragmentSearchNavMenuBinding
    private val viewModel by viewModels<SearchNavMenuViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchNavMenuBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val dividerItemDecoration = DividerItemDecoration(
            binding.searchTermRecyclerView.context,
            LinearLayoutManager.VERTICAL
        )

        binding.searchTermRecyclerView.addItemDecoration(dividerItemDecoration)

        binding.searchTermRecyclerView.adapter = SearchTermListRecyclerAdapter(
            SearchTermListRecyclerAdapter.ClickListener {
                viewModel.onTermSelected(it)
            })

        return binding.root
    }
}