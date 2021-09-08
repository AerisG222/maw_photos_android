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
import us.mikeandwan.photos.services.UploadJobService
import android.database.sqlite.SQLiteOpenHelper
import us.mikeandwan.photos.services.UpdateCategoriesJobService

class UpdateCategoriesJobScheduler(app: Application?, scheduler: JobScheduler?) :
    BaseJobScheduler(app, scheduler) {
    fun schedule(forceReschedule: Boolean, milliseconds: Long) {
        var ji = getPendingJob(MawApplication.JOB_ID_UPDATE_CATEGORY)
        if (forceReschedule) {
            if (ji != null) {
                _scheduler.cancel(MawApplication.JOB_ID_UPDATE_CATEGORY)
            }
        } else {
            if (ji != null) {
                return
            }
        }
        val componentName =
            ComponentName(_app.applicationContext, UpdateCategoriesJobService::class.java)
        ji = JobInfo.Builder(MawApplication.JOB_ID_UPDATE_CATEGORY, componentName)
            .setPeriodic(milliseconds)
            .setPersisted(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()
        _scheduler.schedule(ji)
    }
}