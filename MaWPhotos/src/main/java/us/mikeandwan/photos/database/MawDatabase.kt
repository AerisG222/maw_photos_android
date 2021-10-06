package us.mikeandwan.photos.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ActiveId::class,
        CategoryPreference::class,
        PhotoCategory::class
    ],
    version = 5
)
abstract class MawDatabase : RoomDatabase() {
    abstract fun activeIdDao(): ActiveIdDao
    abstract fun categoryPreferenceDao(): CategoryPreferenceDao
    abstract fun photoCategoryDao(): PhotoCategoryDao
}