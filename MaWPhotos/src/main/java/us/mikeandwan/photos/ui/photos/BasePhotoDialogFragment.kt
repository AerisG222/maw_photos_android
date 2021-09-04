package us.mikeandwan.photos.ui.photos

import android.app.DialogFragment
import android.content.Context
import us.mikeandwan.photos.models.Photo
import us.mikeandwan.photos.ui.photos.IPhotoActivity

open class BasePhotoDialogFragment : DialogFragment() {
    override fun getContext(): Context {
        return activity.baseContext
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
        get() = photoActivity.currentPhoto
}