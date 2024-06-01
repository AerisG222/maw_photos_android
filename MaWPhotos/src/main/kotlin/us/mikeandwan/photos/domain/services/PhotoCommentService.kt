package us.mikeandwan.photos.domain.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.domain.PhotoRepository
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.PhotoComment
import javax.inject.Inject

class PhotoCommentService @Inject constructor (
    private val photoRepository: PhotoRepository
) {
    private val _comments = MutableStateFlow(emptyList<PhotoComment>())
    val comments = _comments.asStateFlow()

    suspend fun fetchCommentDetails(photoId: Int) {
        photoRepository.getComments(photoId)
            .map {
                when (it) {
                    is ExternalCallStatus.Loading -> emptyList()
                    is ExternalCallStatus.Error -> emptyList()
                    is ExternalCallStatus.Success -> it.result
                }
            }
            .collect { _comments.value = it }
    }

    suspend fun addComment(photoId: Int, comment: String) {
        if(comment.isNotBlank()) {
            photoRepository.addComment(photoId, comment)
                .collect { result ->
                    if (result is ExternalCallStatus.Success) {
                        _comments.value = result.result
                    }
                }
        }
    }
}
