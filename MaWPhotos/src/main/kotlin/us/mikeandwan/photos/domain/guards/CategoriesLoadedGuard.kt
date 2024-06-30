package us.mikeandwan.photos.domain.guards

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import us.mikeandwan.photos.domain.ErrorRepository
import us.mikeandwan.photos.domain.MediaCategoryRepository
import javax.inject.Inject

class CategoriesLoadedGuard @Inject constructor (
    private val mediaCategoryRepository: MediaCategoryRepository,
    private val errorRepository: ErrorRepository
) : IGuard {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var allowLoad = true
    private val _status = MutableStateFlow<GuardStatus>(GuardStatus.NotInitialized)
    override val status = _status.asStateFlow()

    override fun initializeGuard() {
        coroutineScope.launch {
            mediaCategoryRepository
                .getMostRecentYear()
                .map {
                    if (it != null && it > 0) {
                        _status.value = GuardStatus.Passed
                    } else {
                        if (allowLoad) {
                            allowLoad = false
                            mediaCategoryRepository.getNewCategories()
                        } else {
                            errorRepository.showError("Unable to load categories")
                            _status.value = GuardStatus.Failed
                        }
                    }
                }.collect { }
        }
    }
}
