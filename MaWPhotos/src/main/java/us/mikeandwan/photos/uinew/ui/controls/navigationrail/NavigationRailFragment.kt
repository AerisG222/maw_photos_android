package us.mikeandwan.photos.uinew.ui.controls.navigationrail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.databinding.FragmentNavigationRailBinding

@AndroidEntryPoint
class NavigationRailFragment : Fragment() {
    companion object {
        fun newInstance() = NavigationRailFragment()
    }

    private lateinit var binding: FragmentNavigationRailBinding
    private val viewModel by viewModels<NavigationRailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNavigationRailBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }
}