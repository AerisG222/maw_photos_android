package us.mikeandwan.photos.uinew.ui.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentPhotoBinding
import us.mikeandwan.photos.uinew.ui.photodetail.PhotoDetailBottomSheetFragment

@AndroidEntryPoint
class PhotoFragment : Fragment() {
    companion object {
        fun newInstance() = PhotoFragment()

        const val TAG = "ModalBottomSheet"
    }

    private lateinit var binding: FragmentPhotoBinding
    val viewModel by viewModels<PhotoViewModel>()

    private val pageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.updateActivePhoto(position)
            super.onPageSelected(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.pager.adapter = PhotoFragmentStateAdapter(viewModel.photos, this)
        binding.pager.registerOnPageChangeCallback(pageChangeCallback)

        binding.info.setOnClickListener {
            val modalBottomSheet = PhotoDetailBottomSheetFragment()
            modalBottomSheet.show(childFragmentManager, TAG)
        }

        initStateObservers()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activePhotoIndex
                    .onEach {
                        delay(1)
                        binding.pager.currentItem = it
                    }
                    .launchIn(this)
            }
        }
    }

    fun setSourceData(vm: IPhotoListViewModel) {
        viewModel.updatePhotoList(vm.photoList, vm.activePhoto.value)
    }
}