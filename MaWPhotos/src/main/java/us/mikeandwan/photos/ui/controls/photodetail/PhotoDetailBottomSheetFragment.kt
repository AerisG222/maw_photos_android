package us.mikeandwan.photos.ui.controls.photodetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentPhotoDetailBottomSheetBinding

@ExperimentalCoroutinesApi
class PhotoDetailBottomSheetFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAB_INDEX_RATING = 0
        const val TAB_INDEX_COMMENTS = 1
        const val TAB_INDEX_EXIF = 2

        fun newInstance() = PhotoDetailBottomSheetFragment()
    }

    private var modalHandler: IHandleModalClose? = null
    private lateinit var binding: FragmentPhotoDetailBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoDetailBottomSheetBinding.inflate(inflater)
        binding.pager.adapter = PhotoDetailPagerAdapter(this)

        TabLayoutMediator(binding.tablayout, binding.pager) { tab, position ->
            when(position) {
                TAB_INDEX_EXIF -> tab.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_tune)
                TAB_INDEX_COMMENTS -> tab.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_comment_white)
                TAB_INDEX_RATING -> tab.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_star)
            }
        }.attach()

        return binding.root
    }

    fun setModalCloseHandler(handler: IHandleModalClose) {
        modalHandler = handler
    }

    override fun onDestroy() {
        modalHandler?.handleModalClose()

        super.onDestroy()
    }
}