package us.mikeandwan.photos.di

import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import android.app.Application
import dagger.Module
import dagger.Provides
import us.mikeandwan.photos.services.MawSQLiteOpenHelper
import us.mikeandwan.photos.services.DatabaseAccessor
import us.mikeandwan.photos.services.PhotoStorage

@Module
@InstallIn(SingletonComponent::class)
class DataStorageModule {
    @Provides
    fun provideMawSQLiteOpenHelper(application: Application?): MawSQLiteOpenHelper {
        return MawSQLiteOpenHelper(application)
    }

    @Provides
    fun provideDatabaseAccessor(sqliteHelper: MawSQLiteOpenHelper?): DatabaseAccessor {
        return DatabaseAccessor(sqliteHelper)
    }

    @Provides
    fun providePhotoStorage(application: Application?): PhotoStorage {
        return PhotoStorage(application)
    }
}