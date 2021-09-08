package us.mikeandwan.photos.services

import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Multipart
import okhttp3.MultipartBody
import us.mikeandwan.photos.services.DatabaseAccessor
import us.mikeandwan.photos.services.PhotoApiClient
import us.mikeandwan.photos.services.PhotoStorage
import io.reactivex.subjects.BehaviorSubject
import kotlin.Throws
import timber.log.Timber
import us.mikeandwan.photos.services.PhotoListType
import android.text.TextUtils
import okhttp3.ResponseBody
import javax.inject.Inject
import android.webkit.MimeTypeMap
import android.os.Environment
import com.commonsware.cwac.provider.StreamProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import us.mikeandwan.photos.services.PhotoApi
import org.apache.commons.io.FilenameUtils
import okhttp3.RequestBody
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
import android.net.Uri
import io.reactivex.Observable
import us.mikeandwan.photos.models.*
import us.mikeandwan.photos.services.UpdateCategoriesJobService
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

class DataServices(
    private val _databaseAccessor: DatabaseAccessor,
    private val _photoApiClient: PhotoApiClient,
    private val _photoStorage: PhotoStorage
) {
    private val _fileQueueSubject: BehaviorSubject<Array<File>>

    @Throws(IOException::class)
    fun addComment(cp: CommentPhoto): ApiCollection<Comment>? {
        Timber.d("started to add comment for photo: %s", cp.photoId)
        _photoApiClient.addComment(cp.photoId, cp.comment)

        return _photoApiClient.getComments(cp.photoId)
    }

    fun downloadCategoryTeaser(category: Category): String {
        Timber.d("started to download teaser for category: %s", category.id)

        return downloadPhoto(category.teaserImage.url)
    }

    fun downloadMdCategoryTeaser(category: Category): String {
        Timber.d("started to download md teaser for category: %s", category.id)

        return downloadPhoto(category.teaserImage.url.replace("/xs", "/md/"))
    }

    fun downloadPhoto(photo: Photo, size: PhotoSize?): String {
        Timber.d("started to download photo: %s", photo.id)
        var path: String? = null

        when (size) {
            PhotoSize.Sm -> path = photo.imageSm.url
            PhotoSize.Md -> path = photo.imageMd.url
            PhotoSize.Xs -> path = photo.imageXs.url
            PhotoSize.Lg -> path = photo.imageLg.url
        }

        return downloadPhoto(path)
    }

    fun getCategoriesForYear(year: Int): List<Category>? {
        Timber.d("started to get categories for year: %s", year)

        return _databaseAccessor.getCategoriesForYear(year)
    }

    @Throws(IOException::class)
    fun getComments(photoId: Int): ApiCollection<Comment>? {
        Timber.d("started to get comments for photo: %s", photoId)

        return _photoApiClient.getComments(photoId)
    }

    @Throws(Exception::class)
    fun getExifData(photoId: Int): ExifData? {
        Timber.d("started to get exif data for photo: %s", photoId)

        return _photoApiClient.getExifData(photoId)
    }

    @Throws(Exception::class)
    fun getPhotoList(type: PhotoListType, categoryId: Int): ApiCollection<Photo>? {
        Timber.d("started to get photo list")

        return _photoApiClient.getPhotos(type, categoryId)
    }

    val photoYears: List<Int>?
        get() = _databaseAccessor.photoYears

    @get:Throws(IOException::class)
    val randomPhoto: Photo?
        get() {
            Timber.d("started to get random photo")
            return _photoApiClient.randomPhoto
        }

    @Throws(IOException::class)
    fun getRandomPhotos(count: Int): ApiCollection<Photo>? {
        Timber.d("started to get random photos")

        return _photoApiClient.getRandomPhotos(count)
    }

    @Throws(IOException::class)
    fun getRating(photoId: Int): Rating? {
        Timber.d("started to get rating for photo: %s", photoId)

        return _photoApiClient.getRatings(photoId)
    }

    @get:Throws(IOException::class)
    val recentCategories: ApiCollection<Category>
        get() {
            Timber.d("started to get recent categories")
            val categories = _photoApiClient.getRecentCategories(_databaseAccessor.latestCategoryId)
            _databaseAccessor.addCategories(categories!!.items)

            return categories
        }

    fun getSharingContentUri(remotePath: String): Uri {
        return _photoStorage.getSharingContentUri(remotePath)
    }

    @Throws(IOException::class)
    fun setRating(photoId: Int, rating: Int): Rating {
        Timber.d("started to set user rating for photo: %s", photoId)
        val averageRating = _photoApiClient.setRating(photoId, rating)
        val rate = Rating()

        if (averageRating != null) {
            rate.averageRating = averageRating
            rate.userRating = Math.round(rating.toFloat()).toShort()
        } else {
            rate.averageRating = 0.toFloat()
            rate.userRating = 0.toShort()
        }

        return rate
    }

    val fileQueueObservable: Observable<Array<File>?>
        get() = _fileQueueSubject

    fun enequeFileToUpload(id: Int, inputStream: InputStream, mimeType: String): Boolean {
        val result = _photoStorage.enqueueFileToUpload(id, inputStream, mimeType)
        if (result) {
            updateQueuedFileSubject()
        }
        return result
    }

    private fun updateQueuedFileSubject() {
        _fileQueueSubject.onNext(_photoStorage.queuedFilesForUpload)
    }

    @Throws(Exception::class)
    fun uploadQueuedFile(file: File) {
        try {
            val result = _photoApiClient.uploadFile(file)
            if (result!!.wasSuccessful) {
                _photoStorage.deleteFileToUpload(file)
                updateQueuedFileSubject()
            } else {
                val err = result.error

                // TODO: service to return error code
                if (err.contains("already exists")) {
                    _photoStorage.deleteFileToUpload(file)
                    updateQueuedFileSubject()
                } else {
                    Timber.e("error reported when uploading file: %s", err)
                    throw Exception("Error uploading file " + file.name)
                }
            }
        } catch (ex: Exception) {
            Timber.e("error uploading file: %s", ex.message)
            throw Exception("Error uploading file " + file.name)
        }
    }

    fun wipeTempFiles() {
        _photoStorage.wipeTempFiles()
    }

    fun wipeCache() {
        _photoStorage.wipeCache()
    }

    private fun downloadPhoto(path: String?): String {
        if (path == null || TextUtils.isEmpty(path)) {
            return _photoStorage.placeholderThumbnail
        }

        val cachePath = "file://" + _photoStorage.getCachePath(path)

        if (_photoStorage.doesExist(path)) {
            return cachePath
        } else {
            try {
                val response = _photoApiClient.downloadPhoto(path)

                if (response != null) {
                    if (response.isSuccessful) {
                        _photoStorage.put(path, response.body())
                        val body = response.body()
                        body?.close()
                        response.close()
                        return cachePath
                    } else {
                        Timber.e(
                            "error downloading file [%s]: status code: %s",
                            path,
                            response.code()
                        )
                    }
                }
            } catch (ex: Exception) {
                Timber.e("error downloading file [%s]: %s", path, ex.message)
            }
        }

        return _photoStorage.placeholderThumbnail
    }

    init {
        val queuedFiles = _photoStorage.queuedFilesForUpload
        _fileQueueSubject = BehaviorSubject.create()
        _fileQueueSubject.onNext(queuedFiles!!)
    }
}