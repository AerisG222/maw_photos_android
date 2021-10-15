package us.mikeandwan.photos.uinew.ui.randomMenu

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
import us.mikeandwan.photos.databinding.FragmentRandomMenuBinding
import us.mikeandwan.photos.databinding.FragmentYearsBinding


@AndroidEntryPoint
class RandomMenuFragment : Fragment() {
    companion object {
        fun newInstance() = RandomMenuFragment()
    }

    private lateinit var binding: FragmentRandomMenuBinding
    private val viewModel by viewModels<RandomMenuViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRandomMenuBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }
}