package us.mikeandwan.photos.services

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import timber.log.Timber
import java.util.*
import javax.inject.Inject

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