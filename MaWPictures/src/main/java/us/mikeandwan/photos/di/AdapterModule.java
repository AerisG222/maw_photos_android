package us.mikeandwan.photos.di;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.ui.categories.ListCategoryRecyclerAdapter;
import us.mikeandwan.photos.ui.categories.ThumbnailCategoryRecyclerAdapter;
import us.mikeandwan.photos.ui.photos.IPhotoActivity;
import us.mikeandwan.photos.ui.photos.FullScreenImageAdapter;
import us.mikeandwan.photos.ui.photos.ThumbnailRecyclerAdapter;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoStorage;


@Module
class AdapterModule {
    @Provides
    @PerActivity
    ListCategoryRecyclerAdapter provideListCategoryRecyclerAdapter(Activity activity,
                                                                   PhotoStorage photoStorage,
                                                                   DataServices dataServices,
                                                                   AuthenticationExceptionHandler authHandler) {
        return new ListCategoryRecyclerAdapter(activity, photoStorage, dataServices, authHandler);
    }


    @Provides
    @PerActivity
    ThumbnailCategoryRecyclerAdapter provideThumbnailCategoryRecyclerAdapter(Activity activity,
                                                                             PhotoStorage photoStorage,
                                                                             DataServices dataServices,
                                                                             AuthenticationExceptionHandler authHandler) {
        return new ThumbnailCategoryRecyclerAdapter(activity, photoStorage, dataServices, authHandler);
    }


    @Provides
    @PerActivity
    FullScreenImageAdapter provideFullScreenImageAdapter(Activity activity,
                                                         PhotoStorage photoStorage,
                                                         DataServices dataServices,
                                                         AuthenticationExceptionHandler authHandler) {
        return new FullScreenImageAdapter((IPhotoActivity) activity, dataServices, photoStorage, authHandler);
    }


    @Provides
    @PerActivity
    ThumbnailRecyclerAdapter provideThumbnailRecyclerAdapter(Activity activity,
                                                             PhotoStorage photoStorage,
                                                             DataServices dataServices,
                                                             AuthenticationExceptionHandler authHandler) {
        return new ThumbnailRecyclerAdapter(activity, photoStorage, dataServices, authHandler);
    }
}
