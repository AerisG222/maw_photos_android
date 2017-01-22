package us.mikeandwan.photos.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.MawSQLiteOpenHelper;
import us.mikeandwan.photos.services.PhotoStorage;


@Module
public class DataStorageModule {
    @Provides
    @Singleton
    public MawSQLiteOpenHelper provideMawSQLiteOpenHelper(Application application) {
        return new MawSQLiteOpenHelper(application);
    }


    @Provides
    @Singleton
    public MawDataManager provideMawDataManager(MawSQLiteOpenHelper sqliteHelper) {
        return new MawDataManager(sqliteHelper);
    }


    @Provides
    @Singleton
    public PhotoStorage providePhotoStorage(Application application) {
        return new PhotoStorage(application);
    }
}
