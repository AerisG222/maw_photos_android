package us.mikeandwan.photos.ui.controls.yearnavmenu

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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentYearMenuBinding


@AndroidEntryPoint
class YearsFragment : Fragment() {
    companion object {
        fun newInstance() = YearsFragment()
    }

    private lateinit var binding: FragmentYearMenuBinding
    private val viewModel by viewModels<YearsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYearMenuBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val dividerItemDecoration = DividerItemDecoration(
            binding.yearRecyclerView.context,
            LinearLayoutManager.VERTICAL
        )

        binding.yearRecyclerView.addItemDecoration(dividerItemDecoration)

        binding.yearRecyclerView.adapter = YearListRecyclerAdapter(
            viewModel.activeYear,
            YearListRecyclerAdapter.ClickListener {
                viewModel.onYearSelected(it)
            })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activeYear.collect {
                    val adapter = binding.yearRecyclerView.adapter
                    binding.yearRecyclerView.adapter = adapter
                }
            }
        }

        return binding.root
    }
}