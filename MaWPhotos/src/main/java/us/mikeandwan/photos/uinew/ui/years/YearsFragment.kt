package us.mikeandwan.photos.uinew.ui.years

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.databinding.FragmentCategoriesBinding
import us.mikeandwan.photos.databinding.FragmentYearsBinding

@AndroidEntryPoint
class YearsFragment : Fragment() {
    companion object {
        fun newInstance() = YearsFragment()
    }

    private lateinit var binding: FragmentYearsBinding
    private val viewModel by viewModels<YearsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYearsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.yearRecyclerView.adapter = YearListRecyclerAdapter(YearListRecyclerAdapter.ClickListener {
            viewModel.onYearSelected(it)
        })

        return binding.root
    }
}