package us.mikeandwan.photos.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.ActiveIdDao
import us.mikeandwan.photos.database.MawDatabase
import us.mikeandwan.photos.database.PhotoCategoryDao
import us.mikeandwan.photos.domain.*
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
    fun providePhotoCategoryRepository(api: PhotoApiClient, db: MawDatabase, photoCategoryDao: PhotoCategoryDao, activeIdDao: ActiveIdDao): PhotoCategoryRepository {
        return PhotoCategoryRepository(api, db, photoCategoryDao, activeIdDao)
    }

    @Provides
    @Singleton
    fun provideRandomPhotoRepository(api: PhotoApiClient, randomPreferenceRepository: RandomPreferenceRepository): RandomPhotoRepository {
        return RandomPhotoRepository(api, randomPreferenceRepository)
    }

    @Provides
    @Singleton
    fun providePhotoListMediator(activeIdRepository: ActiveIdRepository, navigationStateRepository: NavigationStateRepository, photoCategoryRepository: PhotoCategoryRepository, randomPhotoRepository: RandomPhotoRepository, photoPreferenceRepository: PhotoPreferenceRepository, randomPreferenceRepository: RandomPreferenceRepository): PhotoListMediator {
        return PhotoListMediator(activeIdRepository, navigationStateRepository, photoCategoryRepository, randomPhotoRepository, photoPreferenceRepository, randomPreferenceRepository)
    }
}