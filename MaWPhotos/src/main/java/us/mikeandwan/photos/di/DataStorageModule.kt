package us.mikeandwan.photos.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.services.DatabaseAccessor
import us.mikeandwan.photos.services.MawSQLiteOpenHelper
import us.mikeandwan.photos.services.PhotoStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStorageModule {
    @Provides
    @Singleton
    fun provideMawSQLiteOpenHelper(application: Application?): MawSQLiteOpenHelper {
        return MawSQLiteOpenHelper(application)
    }

    @Provides
    @Singleton
    fun provideDatabaseAccessor(sqliteHelper: MawSQLiteOpenHelper?): DatabaseAccessor {
        return DatabaseAccessor(sqliteHelper)
    }

    @Provides
    @Singleton
    fun providePhotoStorage(application: Application?): PhotoStorage {
        return PhotoStorage(application)
    }
}