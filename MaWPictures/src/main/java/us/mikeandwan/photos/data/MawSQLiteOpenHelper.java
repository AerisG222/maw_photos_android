package us.mikeandwan.photos.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import us.mikeandwan.photos.MawApplication;


@EBean(scope = EBean.Scope.Singleton)
class MawSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "maw";

    @Bean
    MawDataManager _dataManager;

    MawSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(MawApplication.LOG_TAG, "> creating sqlite db");

        createYearTable(db);
        createCategoryTable(db);
        createUserTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.d(MawApplication.LOG_TAG, "> upgrading sqlite db from " + String.valueOf(i) + " to " + String.valueOf(i2) + ".");

        if (i < 2) {
            createYearTable(sqLiteDatabase);
            populateYearTable(sqLiteDatabase);
        }

        if (i < 3) {
            updateCategoryTeaserPath(sqLiteDatabase);
        }
    }


    private void updateCategoryTeaserPath(SQLiteDatabase db) {
        String sql = "UPDATE image_category "
                   + "   SET teaser_image_path = REPLACE(teaser_image_path, '/thumbnails/', '/xs/')";

        db.execSQL(sql);
    }

    private void createCategoryTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS image_category ("
            + "    id INTEGER NOT NULL,"
            + "    year INTEGER NOT NULL,"
            + "    name TEXT NOT NULL,"
            + "    has_gps_data INTEGER NOT NULL,"
            + "    teaser_image_width INTEGER NOT NULL,"
            + "    teaser_image_height INTEGER NOT NULL,"
            + "    teaser_image_path TEXT,"
            + "    PRIMARY KEY (id)"
            + ")";

        db.execSQL(sql);
    }


    private void createUserTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS user ("
            + "    username TEXT NOT NULL,"
            + "    password TEXT NOT NULL,"
            + "    PRIMARY KEY (username)"
            + ")";

        db.execSQL(sql);
    }


    private void createYearTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS year ("
            + "    year INTEGER NOT NULL,"
            + "    PRIMARY KEY (year)"
            + ")";

        db.execSQL(sql);
    }


    private void populateYearTable(SQLiteDatabase db) {
        Cursor c = null;
        List<Integer> years = new ArrayList<>();
        String sql = "SELECT DISTINCT year"
            + "  FROM image_category"
            + " ORDER BY year";

        try {
            c = db.rawQuery(sql, null);

            while (c.moveToNext()) {
                years.add(c.getInt(0));
            }
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }

        for (Integer year : years) {
            _dataManager.addYear(year);
        }
    }
}
