package us.mikeandwan.photos.uinew.ui.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.chrisbanes.photoview.PhotoView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
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

    private fun rotatePhoto(direction: Int) {
        // TODO: make this not suck
        val photoView = ((binding.pager.get(0) as RecyclerView).layoutManager?.getChildAt(0) as FrameLayout).children.first() as PhotoView

        photoView.setRotationBy(90f * direction)

        viewModel.rotateComplete()
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activePhotoIndex
                    .onEach {
                        delay(10)  // TODO: how to properly remove the delay hack
                        binding.pager.setCurrentItem(it, true)
                        binding.positionTextView.text = "${it + 1} / ${binding.pager.adapter!!.itemCount}"
                    }
                    .launchIn(this)

                viewModel.rotatePhoto
                    .filter { it != 0 }
                    .onEach {
                        delay(1)
                        rotatePhoto(it)
                    }
                    .launchIn(this)
            }
        }
    }

    fun setSourceData(vm: IPhotoListViewModel) {
        viewModel.updatePhotoList(vm.photoList, vm.activePhoto.value)
    }
}