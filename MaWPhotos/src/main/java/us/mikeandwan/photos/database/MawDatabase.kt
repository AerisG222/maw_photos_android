package us.mikeandwan.photos.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ActiveId::class,
        PhotoCategory::class
    ],
    version = 5
)
abstract class MawDatabase : RoomDatabase() {
    abstract fun photoCategoryDao(): PhotoCategoryDao
    abstract fun activeIdDao(): ActiveIdDao
}