package us.mikeandwan.photos.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.database.*
import us.mikeandwan.photos.domain.*
import us.mikeandwan.photos.domain.services.PhotoCommentService
import us.mikeandwan.photos.domain.services.PhotoExifService
import us.mikeandwan.photos.domain.services.PhotoRatingService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Provides
    @Singleton
    fun provideErrorRepository(): ErrorRepository {
        return ErrorRepository()
    }

    @Provides
    @Singleton
    fun provideApiErrorHandler(
        authService: AuthService,
        errorRepository: ErrorRepository
    ): ApiErrorHandler {
        return ApiErrorHandler(authService, errorRepository)
    }

    @Provides
    @Singleton
    fun providePhotoCategoryRepository(
        api: PhotoApiClient,
        db: MawDatabase,
        photoCategoryDao: PhotoCategoryDao,
        apiErrorHandler: ApiErrorHandler
    ): PhotoCategoryRepository {
        return PhotoCategoryRepository(api, db, photoCategoryDao, apiErrorHandler)
    }

    @Provides
    @Singleton
    fun provideRandomPhotoRepository(
        api: PhotoApiClient,
        randomPreferenceRepository: RandomPreferenceRepository,
        apiErrorHandler: ApiErrorHandler
    ): RandomPhotoRepository {
        return RandomPhotoRepository(api, randomPreferenceRepository, apiErrorHandler)
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
        searchHistoryDao: SearchHistoryDao,
        apiErrorHandler: ApiErrorHandler
    ): SearchRepository {
        return SearchRepository(
            api,
            searchHistoryDao,
            searchPreferenceRepository,
            apiErrorHandler)
    }

    @Provides
    @Singleton
    fun providesPhotoRatingService(
        photoRepository: PhotoRepository
    ): PhotoRatingService {
        return PhotoRatingService(
            photoRepository
        )
    }

    @Provides
    @Singleton
    fun providesPhotoCommentService(
        photoRepository: PhotoRepository
    ): PhotoCommentService {
        return PhotoCommentService(
            photoRepository
        )
    }

    @Provides
    @Singleton
    fun providesPhotoExifService(
        photoRepository: PhotoRepository
    ): PhotoExifService {
        return PhotoExifService(
            photoRepository
        )
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
}
