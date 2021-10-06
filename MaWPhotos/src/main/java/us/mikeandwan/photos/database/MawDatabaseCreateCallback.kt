package us.mikeandwan.photos.database

import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import us.mikeandwan.photos.domain.CategoryDisplayType

class MawDatabaseCreateCallback : Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        db.execSQL("INSERT INTO category_preference (id, display_type) VALUES (1, '${CategoryDisplayType.Grid}')")
        db.execSQL("INSERT INTO notification_preference (id, do_notify, do_vibrate) VALUES (1, 1, 1)")
    }
}