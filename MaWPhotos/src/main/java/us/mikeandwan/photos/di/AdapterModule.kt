package us.mikeandwan.photos.di

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.ui.categories.ListCategoryRecyclerAdapter
import us.mikeandwan.photos.ui.categories.ICategoryListActivity
import us.mikeandwan.photos.ui.categories.ThumbnailCategoryRecyclerAdapter
import us.mikeandwan.photos.ui.photos.FullScreenImageAdapter
import us.mikeandwan.photos.ui.photos.IPhotoActivity
import us.mikeandwan.photos.ui.photos.ThumbnailRecyclerAdapter
import us.mikeandwan.photos.ui.receiver.ReceiverRecyclerAdapter

@Module
@InstallIn(ActivityComponent::class)
internal class AdapterModule {
    @Provides
    @ActivityScoped
    fun provideListCategoryRecyclerAdapter(
        activity: Activity,
        dataServices: DataServices?
    ): ListCategoryRecyclerAdapter {
        return ListCategoryRecyclerAdapter(activity as ICategoryListActivity?, dataServices)
    }

    @Provides
    @ActivityScoped
    fun provideThumbnailCategoryRecyclerAdapter(
        activity: Activity,
        dataServices: DataServices?
    ): ThumbnailCategoryRecyclerAdapter {
        return ThumbnailCategoryRecyclerAdapter(activity as ICategoryListActivity?, dataServices)
    }

    @Provides
    @ActivityScoped
    fun provideFullScreenImageAdapter(
        activity: Activity,
        dataServices: DataServices
    ): FullScreenImageAdapter {
        return FullScreenImageAdapter(activity as IPhotoActivity, dataServices)
    }

    @Provides
    @ActivityScoped
    fun provideThumbnailRecyclerAdapter(
        activity: Activity,
        dataServices: DataServices
    ): ThumbnailRecyclerAdapter {
        return ThumbnailRecyclerAdapter(activity as IPhotoActivity, dataServices)
    }

    @Provides
    @ActivityScoped
    fun provideReceiverRecyclerAdapter(activity: Activity): ReceiverRecyclerAdapter {
        return ReceiverRecyclerAdapter(activity)
    }
}