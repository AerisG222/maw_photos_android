package us.mikeandwan.photos.services;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.MultimediaAsset;


// https://nfrolov.wordpress.com/2014/08/16/android-sqlitedatabase-locking-and-multi-threading/
//   recommends not closing db given its shared nature...
public class DatabaseAccessor {
    private MawSQLiteOpenHelper _dbHelper;


    @Inject
    public DatabaseAccessor(MawSQLiteOpenHelper dbHelper) {
        _dbHelper = dbHelper;
    }


    public int getLatestCategoryId() {
        SQLiteDatabase db = getDatabase();
        int result = 0;
        Cursor c = null;
        String sql = "SELECT MAX(id) FROM image_category";

        try {
            c = db.rawQuery(sql, null);

            c.moveToFirst();

            if (!c.isNull(0)) {
                result = c.getInt(0);
            }
        } catch (Exception ex) {
            Timber.e("error getting latest category id: %s", ex.getMessage());
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }

        return result;
    }


    public List<Category> getCategoriesForYear(int year) {
        SQLiteDatabase db = getDatabase();
        List<Category> result = new ArrayList<>();
        Cursor c = null;
        String sql = "SELECT id, year, name, has_gps_data, teaser_image_width, teaser_image_height, teaser_image_path FROM image_category WHERE year = ? ORDER BY id DESC";

        try {
            c = db.rawQuery(sql, new String[]{String.valueOf(year)});

            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    result.add(BuildCategory(c));
                }
            }
        } catch (Exception ex) {
            Timber.e("error getting categories: %s", ex.getMessage());
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }

        return result;
    }


    public List<Integer> getPhotoYears() {
        SQLiteDatabase db = getDatabase();
        Cursor c = null;
        String sql = "SELECT year FROM year ORDER BY year DESC";
        List<Integer> result = new ArrayList<>();

        try {
            c = db.rawQuery(sql, null);

            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    result.add(c.getInt(0));
                }
            }
        } catch (Exception ex) {
            Timber.e("error getting photo years: %s", ex.getMessage());
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }

        return result;
    }


    public void addCategories(List<Category> categories) {
        List<Integer> years = getPhotoYears();
        SQLiteDatabase db = getDatabase();

        try {
            db.beginTransaction();

            for (Category category : categories) {
                if (!years.contains(category.getYear())) {
                    addYear(db, category.getYear());

                    years.add(category.getYear());
                }

                addCategory(db, category);
            }

            db.setTransactionSuccessful();
        }
        catch(Exception ex) {
            Timber.e("error adding categories: %s", ex.getMessage());
        }
        finally {
            db.endTransaction();
        }
    }


    private void addCategory(SQLiteDatabase db, Category category) {
        ContentValues values = new ContentValues();

        values.put("id", category.getId());
        values.put("year", category.getYear());
        values.put("name", category.getName());
        values.put("has_gps_data", false);
        values.put("teaser_image_width", category.getTeaserImage().getWidth());
        values.put("teaser_image_height", category.getTeaserImage().getHeight());
        values.put("teaser_image_path", category.getTeaserImage().getUrl());

        addSingleRecord(db, "image_category", values);
    }


    private void addYear(SQLiteDatabase db, Integer year) {
        ContentValues values = new ContentValues();

        values.put("year", year);

        addSingleRecord(db, "year", values);
    }


    private Category BuildCategory(Cursor c) {
        Category cat = new Category();
        MultimediaAsset teaser = new MultimediaAsset();

        cat.setTeaserImage(teaser);

        cat.setId(c.getInt(0));
        cat.setYear(c.getInt(1));
        cat.setName(c.getString(2));
       // cat.setHasGpsData(c.getInt(3) == 1);

        teaser.setWidth(c.getInt(4));
        teaser.setHeight(c.getInt(5));
        teaser.setUrl(c.getString(6));

        return cat;
    }


    private void addSingleRecord(SQLiteDatabase db, String tableName, ContentValues values) {
        try {
            long result = db.insert(tableName, null, values);

            if (result == -1) {
                Timber.w("Error when trying to insert a new record to table: %s", tableName);
            }
        } catch (Exception ex) {
            Timber.e("error adding record: %s", ex.getMessage());
        }
    }


    private SQLiteDatabase getDatabase() {
        // no real difference between read/write dbs, so just use writable as we will need to write eventually
        return _dbHelper.getWritableDatabase();
    }
}
