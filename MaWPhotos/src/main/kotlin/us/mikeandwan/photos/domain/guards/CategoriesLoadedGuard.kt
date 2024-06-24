package us.mikeandwan.photos.domain.guards

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import us.mikeandwan.photos.domain.MediaCategoryRepository
import javax.inject.Inject

class CategoriesLoadedGuard @Inject constructor (
    private val mediaCategoryRepository: MediaCategoryRepository
) {
    val status = mediaCategoryRepository
        .getMostRecentYear()
        .map {
            if(it != null && it > 0) {
                GuardStatus.Passed
            } else {
                GuardStatus.Failed
            }
        }
        .onStart { emit(GuardStatus.Unknown) }
}
