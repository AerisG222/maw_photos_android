package us.mikeandwan.photos.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.database.*
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

    private val MIGRATION_1_2 = object: Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE active_id")
        }
    }
}
