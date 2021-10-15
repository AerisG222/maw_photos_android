package us.mikeandwan.photos.uinew.ui.photos

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
import timber.log.Timber
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentPhotosBinding
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridFragment
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridRecyclerAdapter

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

        childFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragmentPhotoList, ImageGridFragment::class.java, null)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photos.collect {
                    val frag = childFragmentManager.fragments.first() as ImageGridFragment

                    frag.setClickHandler(ImageGridRecyclerAdapter.ClickListener {
                        item -> Timber.i("image clicked: $item")
                    })

                    frag.setData(it)
                }
            }
        }

        return binding.root
    }
}