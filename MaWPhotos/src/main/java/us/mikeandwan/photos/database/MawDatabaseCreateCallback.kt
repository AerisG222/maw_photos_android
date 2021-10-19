package us.mikeandwan.photos.database

import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import us.mikeandwan.photos.domain.CategoryDisplayType
import us.mikeandwan.photos.domain.GridThumbnailSize

class MawDatabaseCreateCallback : Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        db.execSQL("INSERT INTO category_preference (id, display_type, grid_thumbnail_size) VALUES (1, '${CategoryDisplayType.Grid}', '${GridThumbnailSize.Medium}')")
        db.execSQL("INSERT INTO notification_preference (id, do_notify, do_vibrate) VALUES (1, 1, 1)")
        db.execSQL("INSERT INTO photo_preference (id, display_toolbar, display_thumbnails, display_top_toolbar, fade_controls, slideshow_interval_seconds, grid_thumbnail_size) VALUES (1, 1, 1, 1, 1, 3, '${GridThumbnailSize.Medium}')")
    }
}