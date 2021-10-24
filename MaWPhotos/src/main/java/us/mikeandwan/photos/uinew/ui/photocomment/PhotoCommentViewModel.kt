package us.mikeandwan.photos.uinew.ui.photocomment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ActiveIdRepository
import us.mikeandwan.photos.domain.PhotoComment
import us.mikeandwan.photos.domain.PhotoRepository
import javax.inject.Inject

@HiltViewModel
class PhotoCommentViewModel @Inject constructor (
    private val activeIdRepository: ActiveIdRepository,
    private val photoRepository: PhotoRepository
): ViewModel() {
    private val _comments = MutableStateFlow(emptyList<PhotoComment>())
    val comments = _comments.asStateFlow()

    fun addComments(comment: String) {
        viewModelScope.launch {
            addCommentsInternal(comment)
        }
    }

    private suspend fun addCommentsInternal(comment: String) {
        val photoId = activeIdRepository.getActivePhotoId().first()

        if(photoId != null && comment.isNotBlank()) {
            _comments.value = photoRepository.addComment(photoId, comment)
        }
    }

    init {
        viewModelScope.launch {
            activeIdRepository
                .getActivePhotoId()
                .filter { it != null }
                .flatMapLatest { photoRepository.getComments(it!!) }
                .map { it ?: emptyList() }
                .onEach { _comments.value = it }
                .launchIn(this)
        }
    }
}