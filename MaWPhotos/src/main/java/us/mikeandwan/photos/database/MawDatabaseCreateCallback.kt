package us.mikeandwan.photos.database

import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize

class MawDatabaseCreateCallback : Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        db.execSQL("INSERT INTO category_preference (id, display_type, grid_thumbnail_size) VALUES (1, '${CategoryDisplayType.Grid}', '${GridThumbnailSize.Medium}')")
        db.execSQL("INSERT INTO notification_preference (id, do_notify, do_vibrate) VALUES (1, 1, 1)")
        db.execSQL("INSERT INTO photo_preference (id, slideshow_interval_seconds, grid_thumbnail_size) VALUES (1, 3, '${GridThumbnailSize.Medium}')")
        db.execSQL("INSERT INTO random_preference (id, slideshow_interval_seconds, grid_thumbnail_size) VALUES (1, 3, '${GridThumbnailSize.Medium}')")
    }
}