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

    suspend fun getRecentCategories(sinceId: Int): ApiResult<ApiCollection<Category>> {
        return makeApiCall(::getRecentCategories.name, suspend { _photoApi.getRecentCategories(sinceId) })
    }

    suspend fun getPhotos(categoryId: Int): ApiResult<ApiCollection<Photo>> {
        return makeApiCall(::getPhotos.name, suspend { _photoApi.getPhotosByCategory(categoryId) })
    }

    suspend fun getRandomPhotos(count: Int): ApiResult<ApiCollection<Photo>> {
        return makeApiCall(::getRandomPhotos.name, suspend { _photoApi.getRandomPhotos(count) })
    }

    suspend fun getExifData(photoId: Int): ApiResult<ExifData> {
        return makeApiCall(::getExifData.name, suspend { _photoApi.getExifData(photoId) })
    }

    suspend fun getComments(photoId: Int): ApiResult<ApiCollection<Comment>> {
        return makeApiCall(::getComments.name, suspend { _photoApi.getComments(photoId) })
    }

    suspend fun getRatings(photoId: Int): ApiResult<Rating> {
        return makeApiCall(::getRatings.name, suspend { _photoApi.getRatings(photoId) })
    }

    suspend fun setRating(photoId: Int, rating: Short): ApiResult<Rating> {
        val rp = RatePhoto(photoId, rating)

        return makeApiCall(::setRating.name, suspend { _photoApi.ratePhoto(photoId, rp) })
    }

    suspend fun addComment(photoId: Int, comment: String): ApiResult<ApiCollection<Comment>> {
        val cp = CommentPhoto(photoId, comment)

        return makeApiCall(::addComment.name, suspend { _photoApi.addCommentForPhoto(photoId, cp) })
    }

    suspend fun searchCategories(query: String, start: Int = 0): ApiResult<SearchResults<SearchResultCategory>> {
        // limit searches to just photos for now
        val search = "-video $query"

        return makeApiCall(::searchCategories.name, suspend { _photoApi.searchCategories(search, start) })
    }

    // https://futurestud.io/tutorials/retrofit-2-how-to-upload-files-to-server
    suspend fun uploadFile(file: File): ApiResult<FileOperationResult> {
        val type = getMediaTypeForFile(file)
        val requestFile = file.asRequestBody(type)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        return makeApiCall(::uploadFile.name, suspend { _photoApi.uploadFile(body) })
    }

    private suspend fun <T> makeApiCall(name: String, apiCall: suspend () -> retrofit2.Response<T>): ApiResult<T> {
        Timber.d("$name starting")

        try {
            val response = apiCall()
            val result = ApiResult.build(response)

            when(result) {
                is ApiResult.Error -> {
                    Timber.w("$name failed: ${result.error}")
                }
                is ApiResult.Success -> {
                    Timber.d("$name succeeded")
                }
                is ApiResult.Empty -> {
                    Timber.d("$name was empty")
                }
            }

            return result
        } catch (t: Throwable) {
            Timber.e(t)

            return ApiResult.Error("$name failed: ${t.message}", t)
        }
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