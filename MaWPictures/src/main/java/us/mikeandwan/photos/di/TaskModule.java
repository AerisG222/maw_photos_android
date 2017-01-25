package us.mikeandwan.photos.di;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.tasks.AddCommentTask;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;
import us.mikeandwan.photos.tasks.GetCategoriesForYearTask;
import us.mikeandwan.photos.tasks.GetCommentsTask;
import us.mikeandwan.photos.tasks.GetExifDataTask;
import us.mikeandwan.photos.tasks.GetPhotoListTask;
import us.mikeandwan.photos.tasks.GetRandomPhotoTask;
import us.mikeandwan.photos.tasks.GetRatingTask;
import us.mikeandwan.photos.tasks.GetRecentCategoriesTask;
import us.mikeandwan.photos.tasks.GetYearsTask;
import us.mikeandwan.photos.tasks.LoginTask;
import us.mikeandwan.photos.tasks.SetRatingTask;


@Module
public class TaskModule {
    // ********************************************************************************************
    // ** start workaround
    // ** this should be able to be removed once the bug mentioned in TaskComponent is fixed
    private final Activity _activity;


    public TaskModule(Activity activity) {
        this._activity = activity;
    }


    @Provides
    @PerActivity
    Activity provideActivity() {
        return this._activity;
    }
    // ** end workaround
    // ********************************************************************************************


    @Provides
    @PerActivity
    AddCommentTask provideAddCommentTask(PhotoApiClient client) {
        return new AddCommentTask(client);
    }


    @Provides
    @PerActivity
    DownloadCategoryTeaserTask provideDownloadCategoryTeaserTask(PhotoApiClient client) {
        return new DownloadCategoryTeaserTask(client);
    }


    @Provides
    @PerActivity
    DownloadPhotoTask provideDownloadImageTask(PhotoApiClient client) {
        return new DownloadPhotoTask(client);
    }


    @Provides
    @PerActivity
    GetCategoriesForYearTask provideGetCategoriesForYearTask(MawDataManager dataManager, PhotoApiClient client) {
        return new GetCategoriesForYearTask(dataManager, client);
    }


    @Provides
    @PerActivity
    GetCommentsTask provideGetCommentsTask(PhotoApiClient client) {
        return new GetCommentsTask(client);
    }


    @Provides
    @PerActivity
    GetExifDataTask provideGetExifDataTask(PhotoApiClient client) {
        return new GetExifDataTask(client);
    }


    @Provides
    @PerActivity
    GetPhotoListTask provideGetPhotoListTask(PhotoApiClient client) {
        return new GetPhotoListTask(client);
    }


    @Provides
    @PerActivity
    GetRandomPhotoTask provideGetRandomPhotoTask(PhotoApiClient client) {
        return new GetRandomPhotoTask(client);
    }


    @Provides
    @PerActivity
    GetRatingTask provideGetRatingTask(PhotoApiClient client) {
        return new GetRatingTask(client);
    }


    @Provides
    @PerActivity
    GetRecentCategoriesTask provideGetRecentCategoriesTask(MawDataManager dataManager, PhotoApiClient client) {
        return new GetRecentCategoriesTask(dataManager, client);
    }


    @Provides
    @PerActivity
    GetYearsTask provideGetYearsTask(MawDataManager dataManager, PhotoApiClient client) {
        return new GetYearsTask(dataManager, client);
    }


    @Provides
    @PerActivity
    LoginTask provideLoginTask(PhotoApiClient client) {
        return new LoginTask(client);
    }


    @Provides
    @PerActivity
    SetRatingTask provideSetRatingTask(PhotoApiClient client) {
        return new SetRatingTask(client);
    }
}
