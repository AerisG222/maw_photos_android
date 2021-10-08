package us.mikeandwan.photos.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.PhotoCategoryDao
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Provides
    @Singleton
    fun provideNavigationStateRepository(): NavigationStateRepository {
        return NavigationStateRepository()
    }

    @Provides
    @Singleton
    fun providePhotoCategoryRepository(api: PhotoApiClient, dao: PhotoCategoryDao): PhotoCategoryRepository {
        return PhotoCategoryRepository(api, dao)
    }
}