package us.mikeandwan.photos.uinew.ui.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import us.mikeandwan.photos.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    companion object {
        fun newInstance() = AboutFragment()
    }

    private lateinit var binding: FragmentAboutBinding
    private val viewModel by viewModels<AboutViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }
}