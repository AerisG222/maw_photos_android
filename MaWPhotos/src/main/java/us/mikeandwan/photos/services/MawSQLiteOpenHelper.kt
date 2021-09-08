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
import android.database.Cursor
import us.mikeandwan.photos.services.UploadJobService
import android.database.sqlite.SQLiteOpenHelper
import us.mikeandwan.photos.services.UpdateCategoriesJobService
import java.util.ArrayList

class MawSQLiteOpenHelper @Inject constructor(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        Timber.d("> creating sqlite db")
        createYearTable(db)
        createCategoryTable(db)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i2: Int) {
        Timber.d("> upgrading sqlite db from %d to %d", i, i2)
        if (i < 2) {
            createYearTable(sqLiteDatabase)
            populateYearTable(sqLiteDatabase)
        }
        if (i < 3) {
            updateCategoryTeaserPath(sqLiteDatabase)
        }
        if (i < 4) {
            removeUserTable(sqLiteDatabase)
        }
    }

    private fun updateCategoryTeaserPath(db: SQLiteDatabase) {
        val sql = ("UPDATE image_category "
                + "   SET teaser_image_path = REPLACE(teaser_image_path, '/thumbnails/', '/xs/')")
        db.execSQL(sql)
    }

    private fun createCategoryTable(db: SQLiteDatabase) {
        val sql = ("CREATE TABLE IF NOT EXISTS image_category ("
                + "    id INTEGER NOT NULL,"
                + "    year INTEGER NOT NULL,"
                + "    name TEXT NOT NULL,"
                + "    has_gps_data INTEGER NOT NULL,"
                + "    teaser_image_width INTEGER NOT NULL,"
                + "    teaser_image_height INTEGER NOT NULL,"
                + "    teaser_image_path TEXT,"
                + "    PRIMARY KEY (id)"
                + ")")
        db.execSQL(sql)
    }

    private fun removeUserTable(db: SQLiteDatabase) {
        val sql = "DROP TABLE IF EXISTS user;"
        db.execSQL(sql)
    }

    private fun createYearTable(db: SQLiteDatabase) {
        val sql = ("CREATE TABLE IF NOT EXISTS year ("
                + "    year INTEGER NOT NULL,"
                + "    PRIMARY KEY (year)"
                + ")")
        db.execSQL(sql)
    }

    private fun populateYearTable(db: SQLiteDatabase) {
        var c: Cursor? = null
        val years: MutableList<Int> = ArrayList()
        val sql = ("SELECT DISTINCT year"
                + "  FROM image_category"
                + " ORDER BY year")
        try {
            c = db.rawQuery(sql, null)
            while (c.moveToNext()) {
                years.add(c.getInt(0))
            }
        } finally {
            if (c != null && !c.isClosed) {
                c.close()
            }
        }
        for (year in years) {
            val values = ContentValues()
            values.put("year", year)
            db.insert("year", null, values)
        }
    }

    companion object {
        private const val DATABASE_VERSION = 4
        private const val DATABASE_NAME = "maw"
    }
}