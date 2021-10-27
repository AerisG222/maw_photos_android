package us.mikeandwan.photos.uinew.ui.random

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentRandomBinding
import us.mikeandwan.photos.uinew.ui.imagegrid.ImageGridFragment

@AndroidEntryPoint
class RandomFragment : Fragment() {
    companion object {
        fun newInstance() = RandomFragment()
    }

    private lateinit var binding: FragmentRandomBinding
    val viewModel by viewModels<RandomViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRandomBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onResume() {
        viewModel.onResume()

        super.onResume()
    }

    override fun onPause() {
        viewModel.onPause()

        super.onPause()
    }
}