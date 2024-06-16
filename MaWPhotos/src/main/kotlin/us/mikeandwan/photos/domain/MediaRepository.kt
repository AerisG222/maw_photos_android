package us.mikeandwan.photos.domain

import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.MediaType
import javax.inject.Inject

class MediaRepository @Inject constructor (
    private val photoRepository: PhotoRepository,
    private val videoRepository: VideoRepository,
) {
    fun getExifData(media: Media) = when(media.type) {
        MediaType.Photo -> photoRepository.getExifData(media.id)
        MediaType.Video -> throw NotImplementedError()
    }

    fun getRating(media: Media) = when(media.type) {
        MediaType.Photo -> photoRepository.getRating(media.id)
        MediaType.Video -> videoRepository.getRating(media.id)
    }

    fun getComments(media: Media) = when(media.type) {
        MediaType.Photo -> photoRepository.getComments(media.id)
        MediaType.Video -> videoRepository.getComments(media.id)
    }

    fun addComment(media: Media, comment: String) = when(media.type) {
        MediaType.Photo -> photoRepository.addComment(media.id, comment)
        MediaType.Video -> videoRepository.addComment(media.id, comment)
    }

    fun setRating(media: Media, rating: Short) = when(media.type) {
        MediaType.Photo -> photoRepository.setRating(media.id, rating)
        MediaType.Video -> videoRepository.setRating(media.id, rating)
    }
}
