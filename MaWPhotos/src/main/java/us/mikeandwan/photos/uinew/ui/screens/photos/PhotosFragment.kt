package us.mikeandwan.photos.uinew.ui.screens.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentPhotosBinding
import us.mikeandwan.photos.uinew.ui.photo.PhotoFragment

@AndroidEntryPoint
class PhotosFragment : Fragment() {
    companion object {
        fun newInstance() = PhotosFragment()
    }

    private lateinit var binding: FragmentPhotosBinding
    val viewModel by viewModels<PhotosViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotosBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if(savedInstanceState == null) {
            initStateObservers()
        }

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activePhoto
                    .filter { it != null }
                    .onEach {
                        val photoFragment = getPhotoFragment()

                        photoFragment?.setSourceData(viewModel)
                    }
                    .launchIn(this)
            }
        }
    }

    private fun getPhotoFragment(): PhotoFragment? {
        return childFragmentManager.findFragmentById(R.id.photoFragment) as PhotoFragment?
    }
}