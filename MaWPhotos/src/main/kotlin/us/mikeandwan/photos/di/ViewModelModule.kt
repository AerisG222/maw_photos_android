package us.mikeandwan.photos.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.ErrorRepository
import us.mikeandwan.photos.domain.FileStorageRepository
import us.mikeandwan.photos.domain.MediaCategoryRepository
import us.mikeandwan.photos.domain.MediaRepository
import us.mikeandwan.photos.domain.guards.AuthGuard
import us.mikeandwan.photos.domain.guards.CategoriesLoadedGuard
import us.mikeandwan.photos.domain.services.MediaCommentService
import us.mikeandwan.photos.domain.services.MediaExifService
import us.mikeandwan.photos.domain.services.MediaListService
import us.mikeandwan.photos.domain.services.MediaRatingService

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    @ViewModelScoped
    fun providesMediaRatingService(
        mediaRepository: MediaRepository
    ): MediaRatingService {
        return MediaRatingService(
            mediaRepository
        )
    }

    @Provides
    @ViewModelScoped
    fun providesMediaCommentService(
        mediaRepository: MediaRepository
    ): MediaCommentService {
        return MediaCommentService(
            mediaRepository
        )
    }

    @Provides
    @ViewModelScoped
    fun providesMediaExifService(
        mediaRepository: MediaRepository
    ): MediaExifService {
        return MediaExifService(
            mediaRepository
        )
    }

    @Provides
    @ViewModelScoped
    fun provideMediaListService(
        mediaCategoryRepository: MediaCategoryRepository,
        fileRepository: FileStorageRepository,
        mediaRatingService: MediaRatingService,
        mediaCommentService: MediaCommentService,
        mediaExifService: MediaExifService
    ): MediaListService {
        return MediaListService(
            mediaCategoryRepository,
            fileRepository,
            mediaRatingService,
            mediaCommentService,
            mediaExifService
        )
    }

    @Provides
    @ViewModelScoped
    fun provideAuthGuard(
        authService: AuthService
    ): AuthGuard {
        return AuthGuard(
            authService
        )
    }

    @Provides
    @ViewModelScoped
    fun provideCategoriesLoadedGuard(
        mediaCategoryRepository: MediaCategoryRepository,
        errorRepository: ErrorRepository
    ): CategoriesLoadedGuard {
        return CategoriesLoadedGuard(
            mediaCategoryRepository,
            errorRepository
        )
    }
}
