package us.mikeandwan.photos.di

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.ui.categories.ListCategoryRecyclerAdapter
import us.mikeandwan.photos.ui.categories.ICategoryListActivity
import us.mikeandwan.photos.ui.categories.ThumbnailCategoryRecyclerAdapter
import us.mikeandwan.photos.ui.photos.FullScreenImageAdapter
import us.mikeandwan.photos.ui.photos.IPhotoActivity
import us.mikeandwan.photos.ui.photos.ThumbnailRecyclerAdapter
import us.mikeandwan.photos.ui.receiver.ReceiverRecyclerAdapter
import javax.inject.Inject

@Module
@InstallIn(ActivityComponent::class)
internal class AdapterModule {
    @Inject lateinit var activity: Activity

    @Provides
    fun provideListCategoryRecyclerAdapter(
        @ActivityContext ctx: ActivityContext,
        dataServices: DataServices?
    ): ListCategoryRecyclerAdapter {
        return ListCategoryRecyclerAdapter(ctx as ICategoryListActivity?, dataServices)
    }

    @Provides
    fun provideThumbnailCategoryRecyclerAdapter(
        dataServices: DataServices?
    ): ThumbnailCategoryRecyclerAdapter {
        return ThumbnailCategoryRecyclerAdapter(activity as ICategoryListActivity?, dataServices)
    }

    @Provides
    fun provideFullScreenImageAdapter(
        dataServices: DataServices
    ): FullScreenImageAdapter {
        return FullScreenImageAdapter(activity as IPhotoActivity, dataServices)
    }

    @Provides
    fun provideThumbnailRecyclerAdapter(
        dataServices: DataServices
    ): ThumbnailRecyclerAdapter {
        return ThumbnailRecyclerAdapter(activity as IPhotoActivity, dataServices)
    }

    @Provides
    fun provideReceiverRecyclerAdapter(): ReceiverRecyclerAdapter {
        return ReceiverRecyclerAdapter(activity)
    }
}