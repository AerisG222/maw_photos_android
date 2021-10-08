package us.mikeandwan.photos.uinew.ui.navigationRail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.databinding.FragmentNavigationRailBinding

@AndroidEntryPoint
class NavigationRailFragment : Fragment() {
    companion object {
        fun newInstance() = NavigationRailFragment()
    }

    private lateinit var binding: FragmentNavigationRailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNavigationRailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }
}