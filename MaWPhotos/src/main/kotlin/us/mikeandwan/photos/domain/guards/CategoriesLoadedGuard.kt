package us.mikeandwan.photos.domain.guards

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import us.mikeandwan.photos.domain.ErrorRepository
import us.mikeandwan.photos.domain.MediaCategoryRepository
import javax.inject.Inject

class CategoriesLoadedGuard @Inject constructor (
    private val mediaCategoryRepository: MediaCategoryRepository,
    errorRepository: ErrorRepository
) {
    private var allowLoad = true

    val status = mediaCategoryRepository
        .getMostRecentYear()
        .map {
            if(it != null && it > 0) {
                GuardStatus.Passed
            } else {
                if(allowLoad) {
                    allowLoad = false
                    mediaCategoryRepository.getNewCategories()
                } else {
                    errorRepository.showError("Unable to load categories")
                    GuardStatus.Failed
                }
            }
        }
        .onStart { emit(GuardStatus.Unknown) }
}
