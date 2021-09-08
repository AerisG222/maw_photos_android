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
import android.database.Cursor
import us.mikeandwan.photos.services.UploadJobService
import android.database.sqlite.SQLiteOpenHelper
import us.mikeandwan.photos.models.*
import us.mikeandwan.photos.services.UpdateCategoriesJobService
import java.lang.Exception
import java.util.ArrayList

// https://nfrolov.wordpress.com/2014/08/16/android-sqlitedatabase-locking-and-multi-threading/
//   recommends not closing db given its shared nature...
class DatabaseAccessor @Inject constructor(private val _dbHelper: MawSQLiteOpenHelper) {
    val latestCategoryId: Int
        get() {
            val db = database
            var result = 0
            var c: Cursor? = null
            val sql = "SELECT MAX(id) FROM image_category"

            try {
                c = db.rawQuery(sql, null)
                c.moveToFirst()
                if (!c.isNull(0)) {
                    result = c.getInt(0)
                }
            } catch (ex: Exception) {
                Timber.e("error getting latest category id: %s", ex.message)
            } finally {
                if (c != null && !c.isClosed) {
                    c.close()
                }
            }

            return result
        }

    fun getCategoriesForYear(year: Int): List<Category> {
        val db = database
        val result: MutableList<Category> = ArrayList()
        var c: Cursor? = null
        val sql =
            "SELECT id, year, name, has_gps_data, teaser_image_width, teaser_image_height, teaser_image_path FROM image_category WHERE year = ? ORDER BY id DESC"

        try {
            c = db.rawQuery(sql, arrayOf(year.toString()))
            if (c.count > 0) {
                while (c.moveToNext()) {
                    result.add(BuildCategory(c))
                }
            }
        } catch (ex: Exception) {
            Timber.e("error getting categories: %s", ex.message)
        } finally {
            if (c != null && !c.isClosed) {
                c.close()
            }
        }

        return result
    }

    val photoYears: MutableList<Int>
        get() {
            val db = database
            var c: Cursor? = null
            val sql = "SELECT year FROM year ORDER BY year DESC"
            val result: MutableList<Int> = ArrayList()

            try {
                c = db.rawQuery(sql, null)
                if (c.count > 0) {
                    while (c.moveToNext()) {
                        result.add(c.getInt(0))
                    }
                }
            } catch (ex: Exception) {
                Timber.e("error getting photo years: %s", ex.message)
            } finally {
                if (c != null && !c.isClosed) {
                    c.close()
                }
            }

            return result
        }

    fun addCategories(categories: List<Category?>) {
        val years = photoYears
        val db = database

        try {
            db.beginTransaction()
            for (category in categories) {
                if (!years.contains(category!!.year)) {
                    addYear(db, category.year)
                    years.add(category.year)
                }
                addCategory(db, category)
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Timber.e("error adding categories: %s", ex.message)
        } finally {
            db.endTransaction()
        }
    }

    private fun addCategory(db: SQLiteDatabase, category: Category?) {
        val values = ContentValues()
        values.put("id", category!!.id)
        values.put("year", category.year)
        values.put("name", category.name)
        values.put("has_gps_data", false)
        values.put("teaser_image_width", category.teaserImage.width)
        values.put("teaser_image_height", category.teaserImage.height)
        values.put("teaser_image_path", category.teaserImage.url)
        addSingleRecord(db, "image_category", values)
    }

    private fun addYear(db: SQLiteDatabase, year: Int) {
        val values = ContentValues()
        values.put("year", year)
        addSingleRecord(db, "year", values)
    }

    private fun BuildCategory(c: Cursor?): Category {
        val cat = Category()
        val teaser = MultimediaAsset()
        cat.teaserImage = teaser
        cat.id = c!!.getInt(0)
        cat.year = c.getInt(1)
        cat.name = c.getString(2)
        // cat.setHasGpsData(c.getInt(3) == 1);
        teaser.width = c.getInt(4)
        teaser.height = c.getInt(5)
        teaser.url = c.getString(6)

        return cat
    }

    private fun addSingleRecord(db: SQLiteDatabase, tableName: String, values: ContentValues) {
        try {
            val result = db.insert(tableName, null, values)
            if (result == -1L) {
                Timber.w("Error when trying to insert a new record to table: %s", tableName)
            }
        } catch (ex: Exception) {
            Timber.e("error adding record: %s", ex.message)
        }
    }

    // no real difference between read/write dbs, so just use writable as we will need to write eventually
    private val database: SQLiteDatabase
        private get() =// no real difference between read/write dbs, so just use writable as we will need to write eventually
            _dbHelper.writableDatabase
}