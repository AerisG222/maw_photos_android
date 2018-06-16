package us.mikeandwan.photos.di;

import android.app.Application;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.services.DatabaseAccessor;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.services.UpdateCategoriesJobScheduler;
import us.mikeandwan.photos.services.UpdateCategoriesJobService;


@Module
public class ApplicationModule {
    private Application _application;


    public ApplicationModule(Application application) {
        _application = application;
    }


    @Provides
    @Singleton
    Application provideApplication() {
        return _application;
    }


    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }


    @Provides
    @Singleton
    JobScheduler provideJobScheduler(Application application) {
        return (JobScheduler) application.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }


    @Provides
    @Singleton
    UpdateCategoriesJobScheduler provideUpdateCategoriesJobScheduler(JobScheduler jobScheduler) {
        return new UpdateCategoriesJobScheduler(jobScheduler);
    }


    @Provides
    @Singleton
    DataServices provideDataServices(DatabaseAccessor databaseAccessor,
                                     PhotoApiClient photoApiClient,
                                     PhotoStorage photoStorage) {
        return new DataServices(databaseAccessor, photoApiClient, photoStorage);
    }
}
