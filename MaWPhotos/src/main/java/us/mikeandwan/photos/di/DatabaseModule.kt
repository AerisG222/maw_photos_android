package us.mikeandwan.photos.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.database.*
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
            "maw"
        )
            .fallbackToDestructiveMigration()
            .addCallback(MawDatabaseCreateCallback())
            .build()
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
    fun provideCategoryPreferenceDao(mawDatabase: MawDatabase): CategoryPreferenceDao {
        return mawDatabase.categoryPreferenceDao()
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
}