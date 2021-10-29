package us.mikeandwan.photos.di

import android.app.Application
import androidx.preference.PreferenceDataStore
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.*
import us.mikeandwan.photos.domain.*
import us.mikeandwan.photos.services.DatabaseAccessor
import us.mikeandwan.photos.services.MawSQLiteOpenHelper
import us.mikeandwan.photos.services.PhotoStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStorageModule {
    @Provides
    @Singleton
    fun providePreferenceDataStore(
        categoryPreferenceRepository: CategoryPreferenceRepository,
        notificationPreferenceRepository: NotificationPreferenceRepository,
        photoPreferenceRepository: PhotoPreferenceRepository,
        randomPreferenceRepository: RandomPreferenceRepository
    ): PreferenceDataStore {
        return MawPreferenceDataStore(
            categoryPreferenceRepository,
            notificationPreferenceRepository,
            photoPreferenceRepository,
            randomPreferenceRepository)
    }

    @Provides
    @Singleton
    fun provideMawDatabase(application: Application): MawDatabase {
        return Room.databaseBuilder(
                application,
                MawDatabase::class.java,
                "maw"
            )
            .fallbackToDestructiveMigration()
            .addCallback(MawDatabaseCreateCallback())
            .build()
    }

    @Provides
    @Singleton
    fun provideCategoryPreferenceRepository(categoryPreferenceDao: CategoryPreferenceDao): CategoryPreferenceRepository {
        return CategoryPreferenceRepository(categoryPreferenceDao)
    }

    @Provides
    @Singleton
    fun provideCategoryPreferenceDao(mawDatabase: MawDatabase): CategoryPreferenceDao {
        return mawDatabase.categoryPreferenceDao()
    }

    @Provides
    @Singleton
    fun provideNotificationPreferenceRepository(notificationPreferenceDao: NotificationPreferenceDao): NotificationPreferenceRepository {
        return NotificationPreferenceRepository(notificationPreferenceDao)
    }

    @Provides
    @Singleton
    fun provideNotificationPreferenceDao(mawDatabase: MawDatabase): NotificationPreferenceDao {
        return mawDatabase.notificationPreferenceDao()
    }

    @Provides
    @Singleton
    fun providePhotoPreferenceRepository(photoPreferenceDao: PhotoPreferenceDao): PhotoPreferenceRepository {
        return PhotoPreferenceRepository(photoPreferenceDao)
    }

    @Provides
    @Singleton
    fun providePhotoPreferenceDao(mawDatabase: MawDatabase): PhotoPreferenceDao {
        return mawDatabase.photoPreferenceDao()
    }

    @Provides
    @Singleton
    fun provideRandomPreferenceRepository(randomPreferenceDao: RandomPreferenceDao): RandomPreferenceRepository {
        return RandomPreferenceRepository(randomPreferenceDao)
    }

    @Provides
    @Singleton
    fun provideRandomPreferenceDao(mawDatabase: MawDatabase): RandomPreferenceDao {
        return mawDatabase.randomPreferenceDao()
    }

    @Provides
    @Singleton
    fun providePhotoCategoryDao(mawDatabase: MawDatabase): PhotoCategoryDao {
        return mawDatabase.photoCategoryDao()
    }

    @Provides
    @Singleton
    fun provideActiveIdDao(mawDatabase: MawDatabase): ActiveIdDao {
        return mawDatabase.activeIdDao()
    }

    @Provides
    @Singleton
    fun provideAuthorizationDao(mawDatabase: MawDatabase): AuthorizationDao {
        return mawDatabase.authorizationDao()
    }

    @Provides
    @Singleton
    fun provideMawSQLiteOpenHelper(application: Application): MawSQLiteOpenHelper {
        return MawSQLiteOpenHelper(application)
    }

    @Provides
    @Singleton
    fun provideDatabaseAccessor(sqliteHelper: MawSQLiteOpenHelper): DatabaseAccessor {
        return DatabaseAccessor(sqliteHelper)
    }

    @Provides
    @Singleton
    fun providePhotoStorage(application: Application): PhotoStorage {
        return PhotoStorage(application)
    }
}