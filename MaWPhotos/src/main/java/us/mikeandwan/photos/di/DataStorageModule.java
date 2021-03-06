package us.mikeandwan.photos.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.services.DatabaseAccessor;
import us.mikeandwan.photos.services.MawSQLiteOpenHelper;
import us.mikeandwan.photos.services.PhotoStorage;


@Module
public class DataStorageModule {
    @Provides
    @Singleton
    MawSQLiteOpenHelper provideMawSQLiteOpenHelper(Application application) {
        return new MawSQLiteOpenHelper(application);
    }


    @Provides
    @Singleton
    DatabaseAccessor provideDatabaseAccessor(MawSQLiteOpenHelper sqliteHelper) {
        return new DatabaseAccessor(sqliteHelper);
    }


    @Provides
    @Singleton
    PhotoStorage providePhotoStorage(Application application) {
        return new PhotoStorage(application);
    }
}
