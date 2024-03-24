package us.mikeandwan.photos.ui.controls.photodetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import us.mikeandwan.photos.databinding.FragmentPhotoDetailBottomSheetBinding
import us.mikeandwan.photos.ui.theme.AppTheme

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
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    PhotoDetailBottomSheet()
                }
            }
        }
    }

    fun setModalCloseHandler(handler: IHandleModalClose) {
        modalHandler = handler
    }

    override fun onDestroy() {
        modalHandler?.handleModalClose()

        super.onDestroy()
    }
}