package us.mikeandwan.photos.di;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.ui.categories.ListCategoryRecyclerAdapter;
import us.mikeandwan.photos.ui.categories.ThumbnailCategoryRecyclerAdapter;
import us.mikeandwan.photos.ui.photos.FullScreenImageAdapter;
import us.mikeandwan.photos.ui.photos.IPhotoActivity;
import us.mikeandwan.photos.ui.photos.ThumbnailRecyclerAdapter;


@Module
class AdapterModule {
    @Provides
    @PerActivity
    ListCategoryRecyclerAdapter provideListCategoryRecyclerAdapter(Activity activity,
                                                                   DataServices dataServices,
                                                                   AuthenticationExceptionHandler authHandler) {
        return new ListCategoryRecyclerAdapter(activity, dataServices, authHandler);
    }


    @Provides
    @PerActivity
    ThumbnailCategoryRecyclerAdapter provideThumbnailCategoryRecyclerAdapter(Activity activity,
                                                                             DataServices dataServices,
                                                                             AuthenticationExceptionHandler authHandler) {
        return new ThumbnailCategoryRecyclerAdapter(activity, dataServices, authHandler);
    }


    @Provides
    @PerActivity
    FullScreenImageAdapter provideFullScreenImageAdapter(Activity activity,
                                                         DataServices dataServices,
                                                         AuthenticationExceptionHandler authHandler) {
        return new FullScreenImageAdapter((IPhotoActivity) activity, dataServices, authHandler);
    }


    @Provides
    @PerActivity
    ThumbnailRecyclerAdapter provideThumbnailRecyclerAdapter(Activity activity,
                                                             DataServices dataServices,
                                                             AuthenticationExceptionHandler authHandler) {
        return new ThumbnailRecyclerAdapter((IPhotoActivity) activity, dataServices, authHandler);
    }
}
