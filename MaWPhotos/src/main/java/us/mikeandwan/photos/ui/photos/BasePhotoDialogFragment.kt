package us.mikeandwan.photos.ui.photos

import android.content.Context
import androidx.fragment.app.DialogFragment
import us.mikeandwan.photos.models.Photo

open class BasePhotoDialogFragment : DialogFragment() {
    override fun getContext(): Context {
        return requireActivity().baseContext
    }

    protected val photoActivity: IPhotoActivity
        protected get() = activity as IPhotoActivity

    fun addWork() {
        photoActivity.addWork()
    }

    fun removeWork() {
        photoActivity.removeWork()
    }

    val currentPhoto: Photo
        get() = photoActivity.currentPhoto!!
}