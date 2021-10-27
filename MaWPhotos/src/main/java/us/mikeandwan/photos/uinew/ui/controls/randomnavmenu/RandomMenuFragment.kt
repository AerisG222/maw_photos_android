package us.mikeandwan.photos.uinew.ui.controls.randomnavmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.databinding.FragmentRandomMenuBinding


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