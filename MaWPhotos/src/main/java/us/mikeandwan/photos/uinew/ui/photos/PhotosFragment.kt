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
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentPhotosBinding
import us.mikeandwan.photos.domain.Photo
import us.mikeandwan.photos.uinew.ui.imagegrid.ImageGridFragment
import us.mikeandwan.photos.uinew.ui.imagegrid.ImageGridRecyclerAdapter
import us.mikeandwan.photos.uinew.ui.photo.PhotoFragment

@AndroidEntryPoint
class PhotosFragment : Fragment() {
    companion object {
        fun newInstance() = PhotosFragment()
    }

    private lateinit var binding: FragmentPhotosBinding
    val viewModel by viewModels<PhotosViewModel>()

    private val onPhotoClicked = ImageGridRecyclerAdapter.ClickListener {
        viewModel.setActivePhoto(it.data as Photo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotosBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if(savedInstanceState == null) {
            initGrid()
            initStateObservers()
        } else {
            val frag = getImageGridFragment()

            frag.setClickHandler(onPhotoClicked)
        }

        return binding.root
    }

    // TODO: can we just specify the fragment in the layout? or is this in case we want to support a different view type?
    private fun initGrid() {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragmentPhotoList, ImageGridFragment::class.java, null, "grid")
        }

        childFragmentManager.executePendingTransactions()
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.preferences
                    .combine(viewModel.photos) { preferences, photos -> Pair(preferences, photos) }
                    .onEach { (preferences, photos) ->
                        val imageGridFragment = getImageGridFragment()

                        imageGridFragment.setClickHandler(onPhotoClicked)
                        imageGridFragment.setThumbnailSize(preferences.gridThumbnailSize)
                        imageGridFragment.setData(photos)
                    }
                    .launchIn(this)

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

    private fun getImageGridFragment(): ImageGridFragment {
        return childFragmentManager.findFragmentByTag("grid") as ImageGridFragment
    }

    private fun getPhotoFragment(): PhotoFragment? {
        return childFragmentManager.findFragmentById(R.id.photoFragment) as PhotoFragment?
    }
}