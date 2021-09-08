package us.mikeandwan.photos.services

import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Multipart
import us.mikeandwan.photos.services.DatabaseAccessor
import us.mikeandwan.photos.services.PhotoApiClient
import us.mikeandwan.photos.services.PhotoStorage
import io.reactivex.subjects.BehaviorSubject
import kotlin.Throws
import timber.log.Timber
import us.mikeandwan.photos.services.PhotoListType
import android.text.TextUtils
import javax.inject.Inject
import android.webkit.MimeTypeMap
import android.os.Environment
import com.commonsware.cwac.provider.StreamProvider
import retrofit2.Retrofit
import us.mikeandwan.photos.services.PhotoApi
import org.apache.commons.io.FilenameUtils
import android.content.SharedPreferences
import net.openid.appauth.AuthState
import androidx.annotation.AnyThread
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationException
import net.openid.appauth.TokenResponse
import net.openid.appauth.RegistrationResponse
import us.mikeandwan.photos.services.AuthStateManager
import org.json.JSONException
import android.app.Application
import android.app.job.JobScheduler
import android.app.job.JobInfo
import us.mikeandwan.photos.services.MawSQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import us.mikeandwan.photos.services.BaseJobScheduler
import us.mikeandwan.photos.MawApplication
import android.content.ComponentName
import us.mikeandwan.photos.services.UploadJobService
import android.database.sqlite.SQLiteOpenHelper
import okhttp3.*
import us.mikeandwan.photos.models.*
import us.mikeandwan.photos.services.UpdateCategoriesJobService
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.URL

class PhotoApiClient @Inject constructor(
    private val _httpClient: OkHttpClient,
    retrofit: Retrofit
) {
    private val _photoApi: PhotoApi
    private val _map = MimeTypeMap.getSingleton()

    @Throws(IOException::class)
    fun getRecentCategories(sinceId: Int): ApiCollection<Category>? {
        Timber.d("getRecentCategories starting")
        val response = _photoApi.getRecentCategories(sinceId).execute()
        val result = ApiResult(response)

        if (!result.isSuccess) {
            Timber.w("getRecentCategories failed: %s", result.error)
            return null
        }

        Timber.d("getRecentCategories succeeded: %d categories found", result.result!!.count)

        return result.result
    }

    @Throws(Exception::class)
    fun getPhotos(type: PhotoListType?, categoryId: Int): ApiCollection<Photo>? {
        Timber.d("getPhotos starting")
        val response = _photoApi.getPhotosByCategory(categoryId).execute()
        val result = ApiResult(response)

        if (!result.isSuccess) {
            Timber.w("getPhotos failed: %s", result.error)
            return null
        }

        Timber.d("getRecentCategories succeeded: %d categories found", result.result!!.count)

        return result.result
    }

    @get:Throws(IOException::class)
    val randomPhoto: Photo?
        get() {
            Timber.d("getRandomPhoto starting")
            val response = _photoApi.randomPhoto.execute()
            val result = ApiResult(response)

            if (!result.isSuccess) {
                Timber.w("getRandomPhoto failed: %s", result.error)
                return null
            }

            Timber.d("getRandomPhoto succeeded")

            return result.result
        }

    @Throws(IOException::class)
    fun getRandomPhotos(count: Int): ApiCollection<Photo>? {
        Timber.d("getRandomPhotos starting")
        val response = _photoApi.getRandomPhotos(count).execute()
        val result = ApiResult(response)

        if (!result.isSuccess) {
            Timber.w("getRandomPhotos failed: %s", result.error)
            return null
        }

        Timber.d("getRandomPhotos succeeded")

        return result.result
    }

    @Throws(IOException::class)
    fun getExifData(photoId: Int): ExifData? {
        Timber.d("getExifData starting")
        val response = _photoApi.getExifData(photoId).execute()
        val result = ApiResult(response)
        if (!result.isSuccess) {
            Timber.w("getExifData failed: %s", result.error)
            return null
        }
        Timber.d("getExifData succeeded")
        return result.result
    }

    @Throws(IOException::class)
    fun getComments(photoId: Int): ApiCollection<Comment>? {
        Timber.d("getComments starting")
        val response = _photoApi.getComments(photoId).execute()
        val result = ApiResult(response)

        if (!result.isSuccess) {
            Timber.w("getComments failed: %s", result.error)
            return null
        }

        Timber.d("getComments succeeded, %d comments found.", result.result!!.count)

        return result.result
    }

    @Throws(IOException::class)
    fun getRatings(photoId: Int): Rating? {
        Timber.d("getRatings starting")
        val response = _photoApi.getRatings(photoId).execute()
        val result = ApiResult(response)

        if (!result.isSuccess) {
            Timber.w("getRatings failed: %s", result.error)
            return null
        }

        Timber.d("getRatings succeeded")

        return result.result
    }

    @Throws(IOException::class)
    fun setRating(photoId: Int, rating: Int): Float? {
        val rp = RatePhoto()
        rp.photoId = photoId
        rp.rating = rating
        Timber.d("setRating starting")
        val response = _photoApi.ratePhoto(photoId, rp).execute()
        val result = ApiResult(response)

        if (!result.isSuccess) {
            Timber.w("setRating failed: %s", result.error)
            return null
        }

        Timber.d("setRating succeeded")

        return result.result!!.averageRating
    }

    @Throws(IOException::class)
    fun addComment(photoId: Int, comment: String) {
        val cp = CommentPhoto()
        cp.comment = comment
        cp.photoId = photoId
        Timber.d("addComment starting")
        val response = _photoApi.addCommentForPhoto(photoId, cp).execute()
        val result = ApiResult(response)

        if (!result.isSuccess) {
            Timber.w("addComment failed: %s", result.error)
        }

        Timber.d("addComment succeeded")
    }

    fun downloadPhoto(photoUrl: String): Response? {
        try {
            val url = URL(photoUrl)
            val request = Request.Builder().url(url).build()
            return _httpClient.newCall(request).execute()
        } catch (ex: IOException) {
            Timber.w("Error when getting photo blob: %s", ex.message)
        }

        return null
    }

    // https://futurestud.io/tutorials/retrofit-2-how-to-upload-files-to-server
    @Throws(IOException::class)
    fun uploadFile(file: File): FileOperationResult? {
        try {
            val type =
                MediaType.parse(_map.getMimeTypeFromExtension(FilenameUtils.getExtension(file.name)))
            val requestFile = RequestBody.create(type, file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val response = _photoApi.uploadFile(body).execute()

            if (response.isSuccessful) {
                Timber.w("upload succeeded for file: %s", file.name)
                return response.body()
            } else {
                Timber.w("unable to upload file: %s", file.name)
            }
        } catch (ex: IOException) {
            Timber.w("Error uploading file: %s: %s", file.name, ex.message)
            throw ex
        }

        return null
    }

    init {
        _photoApi = retrofit.create(PhotoApi::class.java)
    }
}