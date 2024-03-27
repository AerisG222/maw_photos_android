package us.mikeandwan.photos.ui.controls.photocomment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoRepository
import us.mikeandwan.photos.domain.models.PhotoComment
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import javax.inject.Inject

@HiltViewModel
class PhotoCommentViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository,
    private val photoRepository: PhotoRepository
): ViewModel() {
    private val _comments = MutableStateFlow(emptyList<PhotoComment>())
    val comments = _comments.asStateFlow()

    private val _newComment = MutableStateFlow("")
    val newComment = _newComment.asStateFlow()

    fun addComment() {
        viewModelScope.launch {
            addCommentsInternal(newComment.value)
        }
    }

    fun setNewComment(text: String) {
        _newComment.value = text
    }

    private suspend fun addCommentsInternal(comment: String) {
        val photoId = activeIdRepository.getActivePhotoId().first()

        if(photoId != null && comment.isNotBlank()) {
            photoRepository.addComment(photoId, comment)
                .collect { result ->
                    if(result is ExternalCallStatus.Success) {
                        _comments.value = result.result
                    }
                }
        }
    }

    init {
        viewModelScope.launch {
            activeIdRepository
                .getActivePhotoId()
                .filter { it != null }
                .flatMapLatest { photoRepository.getComments(it!!) }
                .map {
                    when(it) {
                        is ExternalCallStatus.Loading -> emptyList()
                        is ExternalCallStatus.Error -> emptyList()
                        is ExternalCallStatus.Success -> it.result
                    }
                }
                .onEach { _comments.value = it }
                .launchIn(this)
        }
    }
}