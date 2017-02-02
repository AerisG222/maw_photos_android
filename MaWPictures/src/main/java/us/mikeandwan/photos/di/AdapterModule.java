package us.mikeandwan.photos.di;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.activities.IPhotoActivity;
import us.mikeandwan.photos.models.ui.CategoryRecyclerAdapter;
import us.mikeandwan.photos.models.ui.CategoryThumbnailArrayAdapter;
import us.mikeandwan.photos.models.ui.FullScreenImageAdapter;
import us.mikeandwan.photos.models.ui.ThumbnailRecyclerAdapter;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;


@Module
public class AdapterModule {
    @Provides
    @PerActivity
    CategoryRecyclerAdapter provideCategoryRecyclerAdapter(Activity activity, PhotoStorage photoStorage, DownloadCategoryTeaserTask task, AuthenticationExceptionHandler authHandler) {
        return new CategoryRecyclerAdapter(activity, photoStorage, task, authHandler);
    }


    @Provides
    @PerActivity
    CategoryThumbnailArrayAdapter provideCategoryThumbnailArrayAdapter(Activity activity, PhotoStorage photoStorage, DownloadCategoryTeaserTask task, AuthenticationExceptionHandler authHandler) {
        return new CategoryThumbnailArrayAdapter(activity, photoStorage, task, authHandler);
    }


    @Provides
    @PerActivity
    FullScreenImageAdapter provideFullScreenImageAdapter(Activity activity, PhotoStorage photoStorage, PhotoApiClient photoClient, AuthenticationExceptionHandler authHandler) {
        return new FullScreenImageAdapter((IPhotoActivity) activity, photoStorage, photoClient, authHandler);
    }


    @Provides
    @PerActivity
    ThumbnailRecyclerAdapter provideThumbnailRecyclerAdapter(Activity activity, PhotoStorage photoStorage, DownloadPhotoTask task, AuthenticationExceptionHandler authHandler) {
        return new ThumbnailRecyclerAdapter(activity, photoStorage, task, authHandler);
    }
}
