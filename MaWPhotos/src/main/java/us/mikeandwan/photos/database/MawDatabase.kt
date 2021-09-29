package us.mikeandwan.photos.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PhotoCategory::class
    ],
    version = 5
)
abstract class MawDatabase : RoomDatabase() {
    abstract fun photoCategoryDao(): PhotoCategoryDao
}