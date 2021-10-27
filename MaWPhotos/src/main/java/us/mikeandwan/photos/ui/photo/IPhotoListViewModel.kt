package us.mikeandwan.photos.ui.photo

import kotlinx.coroutines.flow.StateFlow
import us.mikeandwan.photos.domain.Photo

interface IPhotoListViewModel {
    val photoList: StateFlow<List<Photo>>
    val activePhoto: StateFlow<Photo?>
}