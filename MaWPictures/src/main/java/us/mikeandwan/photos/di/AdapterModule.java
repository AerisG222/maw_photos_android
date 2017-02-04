package us.mikeandwan.photos.di;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.ui.categories.ListCategoryRecyclerAdapter;
import us.mikeandwan.photos.ui.categories.ThumbnailCategoryRecyclerAdapter;
import us.mikeandwan.photos.ui.photos.IPhotoActivity;
import us.mikeandwan.photos.ui.photos.FullScreenImageAdapter;
import us.mikeandwan.photos.ui.photos.ThumbnailRecyclerAdapter;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;


@Module
class AdapterModule {
    @Provides
    @PerActivity
    ListCategoryRecyclerAdapter provideListCategoryRecyclerAdapter(Activity activity,
                                                                   PhotoStorage photoStorage,
                                                                   DownloadCategoryTeaserTask task,
                                                                   AuthenticationExceptionHandler authHandler) {
        return new ListCategoryRecyclerAdapter(activity, photoStorage, task, authHandler);
    }


    @Provides
    @PerActivity
    ThumbnailCategoryRecyclerAdapter provideThumbnailCategoryRecyclerAdapter(Activity activity,
                                                                             PhotoStorage photoStorage,
                                                                             DownloadCategoryTeaserTask task,
                                                                             AuthenticationExceptionHandler authHandler) {
        return new ThumbnailCategoryRecyclerAdapter(activity, photoStorage, task, authHandler);
    }


    @Provides
    @PerActivity
    FullScreenImageAdapter provideFullScreenImageAdapter(Activity activity,
                                                         PhotoStorage photoStorage,
                                                         PhotoApiClient photoClient,
                                                         AuthenticationExceptionHandler authHandler) {
        return new FullScreenImageAdapter((IPhotoActivity) activity, photoStorage, photoClient, authHandler);
    }


    @Provides
    @PerActivity
    ThumbnailRecyclerAdapter provideThumbnailRecyclerAdapter(Activity activity,
                                                             PhotoStorage photoStorage,
                                                             DownloadPhotoTask task,
                                                             AuthenticationExceptionHandler authHandler) {
        return new ThumbnailRecyclerAdapter(activity, photoStorage, task, authHandler);
    }
}
