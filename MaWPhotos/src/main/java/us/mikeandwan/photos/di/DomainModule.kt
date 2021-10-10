package us.mikeandwan.photos.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.ActiveIdDao
import us.mikeandwan.photos.database.PhotoCategoryDao
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.NavigationStateRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Provides
    @Singleton
    fun provideActiveIdRepository(activeIdDao: ActiveIdDao): ActiveIdRepository {
        return ActiveIdRepository(activeIdDao)
    }

    @Provides
    @Singleton
    fun provideNavigationStateRepository(activeIdRepository: ActiveIdRepository, photoCategoryRepository: PhotoCategoryRepository): NavigationStateRepository {
        return NavigationStateRepository(activeIdRepository, photoCategoryRepository)
    }

    @Provides
    @Singleton
    fun providePhotoCategoryRepository(api: PhotoApiClient, dao: PhotoCategoryDao): PhotoCategoryRepository {
        return PhotoCategoryRepository(api, dao)
    }
}