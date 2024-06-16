package us.mikeandwan.photos.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object: Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // new video_category table to be created by ROOM
    }
}
