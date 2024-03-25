package us.mikeandwan.photos.ui.controls.photodetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.ui.theme.AppTheme

@AndroidEntryPoint
class PhotoDetailBottomSheetFragment : BottomSheetDialogFragment() {
    companion object {
        fun newInstance() = PhotoDetailBottomSheetFragment()
    }

    private var modalHandler: IHandleModalClose? = null

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