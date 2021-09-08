package us.mikeandwan.photos.services

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
import retrofit2.Call
import retrofit2.http.*
import us.mikeandwan.photos.models.*
import us.mikeandwan.photos.services.UpdateCategoriesJobService

internal interface PhotoApi {
    @GET("photo-categories/recent/{sinceId}")
    fun getRecentCategories(@Path("sinceId") sinceId: Int): Call<ApiCollection<Category>>

    @GET("photos/{photoId}/exif")
    fun getExifData(@Path("photoId") photoId: Int): Call<ExifData>

    @get:GET("photos/random")
    val randomPhoto: Call<Photo>

    @GET("photos/random/{count}")
    fun getRandomPhotos(@Path("count") count: Int): Call<ApiCollection<Photo>>

    @GET("photos/{photoId}/comments")
    fun getComments(@Path("photoId") photoId: Int): Call<ApiCollection<Comment>>

    @GET("photos/{photoId}/rating")
    fun getRatings(@Path("photoId") photoId: Int): Call<Rating>

    @GET("photo-categories/{categoryId}/photos")
    fun getPhotosByCategory(@Path("categoryId") categoryId: Int): Call<ApiCollection<Photo>>

    @PATCH("photos/{photoId}/rating")
    fun ratePhoto(@Path("photoId") photoId: Int, @Body rating: RatePhoto): Call<Rating>

    @POST("photos/{photoId}/comments")
    fun addCommentForPhoto(
        @Path("photoId") photoId: Int,
        @Body commentPhoto: CommentPhoto
    ): Call<ApiCollection<Comment>>

    @Multipart
    @POST("upload/upload")
    fun uploadFile(@Part file: MultipartBody.Part): Call<FileOperationResult>
}