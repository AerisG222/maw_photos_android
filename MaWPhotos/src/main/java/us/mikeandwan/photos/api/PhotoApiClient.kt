package us.mikeandwan.photos.api

import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PhotoApiClient @Inject constructor(
    retrofit: Retrofit
) {
    private val _photoApi: PhotoApi by lazy { retrofit.create(PhotoApi::class.java) }
    private val _mimeMap by lazy { MimeTypeMap.getSingleton() }

    suspend fun getRecentCategories(sinceId: Int): ApiCollection<Category>? {
        return makeApiCall(::getRecentCategories.name, suspend { _photoApi.getRecentCategories(sinceId) })
    }

    suspend fun getPhotos(categoryId: Int): ApiCollection<Photo>? {
        return makeApiCall(::getPhotos.name, suspend { _photoApi.getPhotosByCategory(categoryId) })
    }

    suspend fun getRandomPhotos(count: Int): ApiCollection<Photo>? {
        return makeApiCall(::getRandomPhotos.name, suspend { _photoApi.getRandomPhotos(count) })
    }

    suspend fun getExifData(photoId: Int): ExifData? {
        return makeApiCall(::getExifData.name, suspend { _photoApi.getExifData(photoId) })
    }

    suspend fun getComments(photoId: Int): ApiCollection<Comment>? {
        return makeApiCall(::getComments.name, suspend { _photoApi.getComments(photoId) })
    }

    suspend fun getRatings(photoId: Int): Rating? {
        return makeApiCall(::getRatings.name, suspend { _photoApi.getRatings(photoId) })
    }

    suspend fun setRating(photoId: Int, rating: Short): Float? {
        val rp = RatePhoto(photoId, rating)

        val result = makeApiCall(::setRating.name, suspend { _photoApi.ratePhoto(photoId, rp) })

        return result?.averageRating
    }

    suspend fun addComment(photoId: Int, comment: String) {
        val cp = CommentPhoto(photoId, comment)

        makeApiCall(::addComment.name, suspend { _photoApi.addCommentForPhoto(photoId, cp) })
    }

    // https://futurestud.io/tutorials/retrofit-2-how-to-upload-files-to-server
    suspend fun uploadFile(file: File): FileOperationResult? {
        val type = getMediaTypeForFile(file)
        val requestFile = file.asRequestBody(type)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        return makeApiCall(::uploadFile.name, suspend { _photoApi.uploadFile(body) })
    }

    private suspend fun <T> makeApiCall(name: String, apiCall: suspend () -> retrofit2.Response<T>): T? {
        Timber.d("$name starting")

        val response = apiCall()
        val result = ApiResult(response)

        if (!result.isSuccess) {
            Timber.w("$name failed: ${result.error}")

            return null
        }

        Timber.d("$name succeeded")

        return result.result
    }

    private fun getMediaTypeForFile(file:File): MediaType {
        val mimeType = _mimeMap.getMimeTypeFromExtension(file.extension)
        var type: MediaType? = null

        if(mimeType != null) {
            type = mimeType.toMediaTypeOrNull()
        }

        if(type == null)
        {
            type = "binary/octet-stream".toMediaType()
        }

        return type
    }
}