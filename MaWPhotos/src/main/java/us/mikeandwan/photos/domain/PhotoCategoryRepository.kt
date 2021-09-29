package us.mikeandwan.photos.domain

import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.PhotoCategoryDao
import javax.inject.Inject

class PhotoCategoryRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val dao: PhotoCategoryDao
) {

}