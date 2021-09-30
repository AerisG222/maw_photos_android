package us.mikeandwan.photos.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // delete original db tables
        database.execSQL("DROP TABLE IF EXISTS year")
        database.execSQL("DROP TABLE IF EXISTS image_category")

        // create new room tables (copied directly from MawDatabase_Impl in the generated directory)
        database.execSQL("CREATE TABLE IF NOT EXISTS `photo_category` (`id` INTEGER NOT NULL, `year` INTEGER NOT NULL, `name` TEXT NOT NULL, `teaser_height` INTEGER NOT NULL, `teaser_width` INTEGER NOT NULL, `teaser_url` TEXT NOT NULL, PRIMARY KEY(`id`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_photo_category_year` ON `photo_category` (`year`)")
        database.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        database.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'cc1ded40f4342c59466dbed29b532455')")
    }
}