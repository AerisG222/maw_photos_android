package us.mikeandwan.photos.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.database.*
import us.mikeandwan.photos.database.migrations.MIGRATION_1_2
import us.mikeandwan.photos.database.migrations.MIGRATION_2_3
import us.mikeandwan.photos.database.migrations.MawDatabaseCreateCallback
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideMawDatabase(application: Application): MawDatabase {
        return Room.databaseBuilder(
            application,
            MawDatabase::class.java,
            "us.mikeandwan.photos"
        )
            .addCallback(MawDatabaseCreateCallback())
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthorizationDao(mawDatabase: MawDatabase): AuthorizationDao {
        return mawDatabase.authorizationDao()
    }

    @Provides
    @Singleton
    fun provideCategoryPreferenceDao(mawDatabase: MawDatabase): CategoryPreferenceDao {
        return mawDatabase.categoryPreferenceDao()
    }

    @Provides
    @Singleton
    fun provideMediaCategoryDao(mawDatabase: MawDatabase): MediaCategoryDao {
        return mawDatabase.mediaCategoryDao()
    }

    @Provides
    @Singleton
    fun provideNotificationPreferenceDao(mawDatabase: MawDatabase): NotificationPreferenceDao {
        return mawDatabase.notificationPreferenceDao()
    }

    @Provides
    @Singleton
    fun providePhotoCategoryDao(mawDatabase: MawDatabase): PhotoCategoryDao {
        return mawDatabase.photoCategoryDao()
    }

    @Provides
    @Singleton
    fun providePhotoPreferenceDao(mawDatabase: MawDatabase): PhotoPreferenceDao {
        return mawDatabase.photoPreferenceDao()
    }

    @Provides
    @Singleton
    fun provideRandomPreferenceDao(mawDatabase: MawDatabase): RandomPreferenceDao {
        return mawDatabase.randomPreferenceDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(mawDatabase: MawDatabase): SearchHistoryDao {
        return mawDatabase.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideSearchPreferenceDao(mawDatabase: MawDatabase): SearchPreferenceDao {
        return mawDatabase.searchPreferenceDao()
    }

    @Provides
    @Singleton
    fun provideVideoCategoryDao(mawDatabase: MawDatabase): VideoCategoryDao {
        return mawDatabase.videoCategoryDao()
    }
}
