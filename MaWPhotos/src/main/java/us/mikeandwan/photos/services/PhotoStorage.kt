package us.mikeandwan.photos.services

import retrofit2.http.GET
import us.mikeandwan.photos.models.ApiCollection
import us.mikeandwan.photos.models.ExifData
import retrofit2.http.PATCH
import us.mikeandwan.photos.models.RatePhoto
import retrofit2.http.POST
import us.mikeandwan.photos.models.CommentPhoto
import retrofit2.http.Multipart
import okhttp3.MultipartBody
import us.mikeandwan.photos.models.FileOperationResult
import us.mikeandwan.photos.services.DatabaseAccessor
import us.mikeandwan.photos.services.PhotoApiClient
import us.mikeandwan.photos.services.PhotoStorage
import io.reactivex.subjects.BehaviorSubject
import kotlin.Throws
import timber.log.Timber
import us.mikeandwan.photos.models.PhotoSize
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
import us.mikeandwan.photos.models.ApiResult
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
import us.mikeandwan.photos.models.MultimediaAsset
import us.mikeandwan.photos.services.BaseJobScheduler
import us.mikeandwan.photos.MawApplication
import android.content.ComponentName
import android.content.Context
import us.mikeandwan.photos.services.UploadJobService
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import org.apache.commons.io.FileUtils
import us.mikeandwan.photos.services.UpdateCategoriesJobService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class PhotoStorage @Inject constructor(private val _context: Context) {
    fun put(remotePath: String, body: ResponseBody?) {
        val dir = File(rootPath, remotePath.substring(0, remotePath.lastIndexOf('/')))
        val file = getCachePath(remotePath)
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Timber.e("Error creating photo directory hierarchy: %s", dir.name)
                return
            }
        } else {
            if (file.exists()) {
                return
            }
        }

        // use a unique id here so if we end up downloading the same file 2 times, we don't try to
        // write to the same temp file.  As such, with the final rename, a valid complete file should
        // be put in place
        val tempFile = File(tempRootPath, UUID.randomUUID().toString() + ".tmp")
        try {
            FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(body!!.bytes())
                outputStream.flush()
                outputStream.close()
                tempFile.renameTo(file)
            }
        } catch (e: IOException) {
            Timber.w("Error saving image file: %s", e.message)
        } finally {
            if (tempFile.exists()) {
                try {
                    tempFile.delete()
                } catch (ex: Exception) {
                    // swallow
                }
            }
        }
    }

    fun doesExist(remotePath: String?): Boolean {
        val file = getCachePath(remotePath)
        return file.exists()
    }

    val placeholderThumbnail: String
        get() = "file:///android_asset/placeholder.png"

    fun getCachePath(remotePath: String?): File {
        return File(rootPath, remotePath)
    }

    val queuedFilesForUpload: Array<File>
        get() {
            val uploadDir = uploadDir
            return uploadDir.listFiles()
        }

    fun enqueueFileToUpload(id: Int, inputStream: InputStream, mimeType: String): Boolean {
        val map = MimeTypeMap.getSingleton()
        val extension = map.getExtensionFromMimeType(mimeType)
        val file = getNewUploadFilePath(id, mimeType.substring(0, mimeType.indexOf('/')), extension)
        try {
            FileOutputStream(file).use { outputStream ->
                val buf = ByteArray(4096)
                while (true) {
                    val bytesRead = inputStream.read(buf)
                    if (bytesRead == -1) {
                        break
                    }
                    outputStream.write(buf, 0, bytesRead)
                }
                outputStream.flush()
            }
        } catch (e: IOException) {
            Timber.w("Error saving image file: %s", e.message)
            return false
        }
        return true
    }

    fun deleteFileToUpload(file: File) {
        file.delete()
    }

    fun getSharingContentUri(remotePath: String?): Uri {
        val file = File(Environment.DIRECTORY_PICTURES, remotePath)
        return PROVIDER
            .buildUpon()
            .appendPath(StreamProvider.getUriPrefix(AUTHORITY))
            .appendEncodedPath(file.path)
            .build()
    }

    private val rootPath: File?
        private get() = _context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private val uploadDir: File
        private get() {
            val uploadDir = File(rootPath, "__upload")
            if (!uploadDir.exists()) {
                uploadDir.mkdir()
            }
            return uploadDir
        }

    private fun getNewUploadFilePath(id: Int, filePrefix: String, extension: String?): File {
        val uploadDir = uploadDir
        val now = Date()
        val filename =
            String.format(Locale.ENGLISH, "%s_%s_%d", filePrefix, _dateFormat.format(now), id)
        return File(uploadDir, "$filename.$extension")
    }

    fun wipeTempFiles() {
        try {
            FileUtils.deleteDirectory(tempRootPath)
        } catch (ex: IOException) {
            Timber.e("Unable to delete temp files: %s", ex.message)
        }
    }

    fun wipeCache() {
        try {
            FileUtils.deleteDirectory(rootPath)
        } catch (ex: IOException) {
            Timber.e("Unable to wipe cache: %s", ex.message)
        }
    }

    private val tempRootPath: File
        private get() {
            val dir = File(rootPath.toString() + "/" + "temp")
            if (!dir.exists()) {
                dir.mkdir()
            }
            return dir
        }

    companion object {
        private const val AUTHORITY = "us.mikeandwan.streamprovider"
        private val PROVIDER = Uri.parse("content://" + AUTHORITY)
        private val _dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
    }
}