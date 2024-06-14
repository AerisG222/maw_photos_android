package us.mikeandwan.photos.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.PhotoCategoryRepository
import us.mikeandwan.photos.domain.PhotoRepository
import us.mikeandwan.photos.domain.services.PhotoCommentService
import us.mikeandwan.photos.domain.services.PhotoExifService
import us.mikeandwan.photos.domain.services.PhotoListService
import us.mikeandwan.photos.domain.services.PhotoRatingService

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    @ViewModelScoped
    fun providesPhotoRatingService(
        photoRepository: PhotoRepository
    ): PhotoRatingService {
        return PhotoRatingService(
            photoRepository
        )
    }

    @Provides
    @ViewModelScoped
    fun providesPhotoCommentService(
        photoRepository: PhotoRepository
    ): PhotoCommentService {
        return PhotoCommentService(
            photoRepository
        )
    }

    @Provides
    @ViewModelScoped
    fun providesPhotoExifService(
        photoRepository: PhotoRepository
    ): PhotoExifService {
        return PhotoExifService(
            photoRepository
        )
    }

    @Provides
    @ViewModelScoped
    fun providePhotoListService(
        photoCategoryRepository: PhotoCategoryRepository,
        fileRepository: FileStorageRepository,
        photoRatingService: PhotoRatingService,
        photoCommentService: PhotoCommentService,
        photoExifService: PhotoExifService
    ): PhotoListService {
        return PhotoListService(
            photoCategoryRepository,
            fileRepository,
            photoRatingService,
            photoCommentService,
            photoExifService
        )
    }
}
