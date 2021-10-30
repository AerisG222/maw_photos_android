package us.mikeandwan.photos.uiold.photos

import us.mikeandwan.photos.models.Photo

interface IPhotoActivity {
    fun addWork()
    fun removeWork()
    fun onApiException(throwable: Throwable?)
    val currentPhoto: Photo?
    val photoList: List<Photo>?
}