package us.mikeandwan.photos.uiold.photos

import android.content.Context
import androidx.fragment.app.DialogFragment
import us.mikeandwan.photos.models.Photo

open class BasePhotoDialogFragment : DialogFragment() {
    override fun getContext(): Context {
        return requireActivity()
    }

    protected val photoActivity: IPhotoActivity
        get() = activity as IPhotoActivity

    fun addWork() {
        photoActivity.addWork()
    }

    fun removeWork() {
        photoActivity.removeWork()
    }

    val currentPhoto: Photo
        get() = photoActivity.currentPhoto!!
}