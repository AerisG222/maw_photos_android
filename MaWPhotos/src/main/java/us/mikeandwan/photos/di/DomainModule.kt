package us.mikeandwan.photos.di

import android.app.Application
import androidx.preference.PreferenceDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.*
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
    fun providePhotoCategoryRepository(
        api: PhotoApiClient,
        db: MawDatabase,
        photoCategoryDao: PhotoCategoryDao,
        activeIdDao: ActiveIdDao
    ): PhotoCategoryRepository {
        return PhotoCategoryRepository(api, db, photoCategoryDao, activeIdDao)
    }

    @Provides
    @Singleton
    fun provideRandomPhotoRepository(
        api: PhotoApiClient,
        randomPreferenceRepository: RandomPreferenceRepository
    ): RandomPhotoRepository {
        return RandomPhotoRepository(api, randomPreferenceRepository)
    }

    @Provides
    @Singleton
    fun providePhotoListMediator(
        activeIdRepository: ActiveIdRepository,
        navigationStateRepository: NavigationStateRepository,
        photoCategoryRepository: PhotoCategoryRepository,
        randomPhotoRepository: RandomPhotoRepository,
        photoPreferenceRepository: PhotoPreferenceRepository,
        randomPreferenceRepository: RandomPreferenceRepository
    ): PhotoListMediator {
        return PhotoListMediator(
            activeIdRepository,
            navigationStateRepository,
            photoCategoryRepository,
            randomPhotoRepository,
            photoPreferenceRepository,
            randomPreferenceRepository)
    }

    @Provides
    @Singleton
    fun provideNavigationStateRepository(
        activeIdRepository: ActiveIdRepository,
        photoCategoryRepository: PhotoCategoryRepository
    ): NavigationStateRepository {
        return NavigationStateRepository(activeIdRepository, photoCategoryRepository)
    }

    @Provides
    @Singleton
    fun provideFileStorageRepository(
        application: Application
    ): FileStorageRepository {
        return FileStorageRepository(application)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        api: PhotoApiClient,
        searchPreferenceRepository: SearchPreferenceRepository,
        searchHistoryDao: SearchHistoryDao
    ): SearchRepository {
        return SearchRepository(
            api,
            searchHistoryDao,
            searchPreferenceRepository)
    }

    @Provides
    @Singleton
    fun provideCategoryPreferenceRepository(categoryPreferenceDao: CategoryPreferenceDao): CategoryPreferenceRepository {
        return CategoryPreferenceRepository(categoryPreferenceDao)
    }

    @Provides
    @Singleton
    fun provideNotificationPreferenceRepository(notificationPreferenceDao: NotificationPreferenceDao): NotificationPreferenceRepository {
        return NotificationPreferenceRepository(notificationPreferenceDao)
    }

    @Provides
    @Singleton
    fun providePhotoPreferenceRepository(photoPreferenceDao: PhotoPreferenceDao): PhotoPreferenceRepository {
        return PhotoPreferenceRepository(photoPreferenceDao)
    }

    @Provides
    @Singleton
    fun provideRandomPreferenceRepository(randomPreferenceDao: RandomPreferenceDao): RandomPreferenceRepository {
        return RandomPreferenceRepository(randomPreferenceDao)
    }

    @Provides
    @Singleton
    fun provideSearchPreferenceRepository(searchPreferenceDao: SearchPreferenceDao): SearchPreferenceRepository {
        return SearchPreferenceRepository(searchPreferenceDao)
    }

    @Provides
    @Singleton
    fun providePreferenceDataStore(
        categoryPreferenceRepository: CategoryPreferenceRepository,
        notificationPreferenceRepository: NotificationPreferenceRepository,
        photoPreferenceRepository: PhotoPreferenceRepository,
        randomPreferenceRepository: RandomPreferenceRepository,
        searchPreferenceRepository: SearchPreferenceRepository
    ): PreferenceDataStore {
        return MawPreferenceDataStore(
            categoryPreferenceRepository,
            notificationPreferenceRepository,
            photoPreferenceRepository,
            randomPreferenceRepository,
            searchPreferenceRepository)
    }
}