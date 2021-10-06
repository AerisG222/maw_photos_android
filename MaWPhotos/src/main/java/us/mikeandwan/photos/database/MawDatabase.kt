package us.mikeandwan.photos.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ActiveId::class,
        CategoryPreference::class,
        NotificationPreference::class,
        PhotoCategory::class,
        PhotoPreference::class,
        SyncPreference::class
    ],
    version = 5
)
abstract class MawDatabase : RoomDatabase() {
    abstract fun activeIdDao(): ActiveIdDao
    abstract fun categoryPreferenceDao(): CategoryPreferenceDao
    abstract fun notificationPreferenceDao(): NotificationPreferenceDao
    abstract fun photoCategoryDao(): PhotoCategoryDao
    abstract fun photoPreferenceDao(): PhotoPreferenceDao
    abstract fun syncPreferenceDao(): SyncPreferenceDao
}