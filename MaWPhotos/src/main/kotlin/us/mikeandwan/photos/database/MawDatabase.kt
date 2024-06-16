package us.mikeandwan.photos.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Authorization::class,
        CategoryPreference::class,
        NotificationPreference::class,
        PhotoCategory::class,
        PhotoPreference::class,
        RandomPreference::class,
        SearchHistory::class,
        SearchPreference::class,
        VideoCategory::class,
    ],
    version = 3
)
@TypeConverters(
    Converters::class
)
abstract class MawDatabase : RoomDatabase() {
    abstract fun authorizationDao(): AuthorizationDao
    abstract fun categoryPreferenceDao(): CategoryPreferenceDao
    abstract fun mediaCategoryDao(): MediaCategoryDao
    abstract fun notificationPreferenceDao(): NotificationPreferenceDao
    abstract fun photoCategoryDao(): PhotoCategoryDao
    abstract fun photoPreferenceDao(): PhotoPreferenceDao
    abstract fun randomPreferenceDao(): RandomPreferenceDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun searchPreferenceDao(): SearchPreferenceDao
    abstract fun videoCategoryDao(): VideoCategoryDao
}
