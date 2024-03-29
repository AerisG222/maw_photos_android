package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.NavigationArea
import javax.inject.Inject

class PhotoListMediator @Inject constructor (
    activeIdRepository: ActiveIdRepository,
    navigationStateRepository: NavigationStateRepository,
    photoCategoryRepository: PhotoCategoryRepository,
    randomPhotoRepository: RandomPhotoRepository,
    photoPreferenceRepository: PhotoPreferenceRepository,
    randomPreferenceRepository: RandomPreferenceRepository
) {
    private val isPhotoScreen = navigationStateRepository.isPhotoScreen
        .filter { it }

    private val navArea = isPhotoScreen
        .flatMapLatest { navigationStateRepository.navArea }

    private val randomPhotos = navArea
        .filter { it == NavigationArea.Random }
        .flatMapLatest { randomPhotoRepository.photos }

    private val categoryPhotos = navArea
        .filter { it == NavigationArea.Category }
        .flatMapLatest { activeIdRepository.getActivePhotoCategoryId() }
        .filter { it != null }
        .distinctUntilChanged()
        .flatMapLatest { photoCategoryRepository.getPhotos(it!!) }
        .map { result ->
            when(result) {
                is ExternalCallStatus.Success -> result.result
                else -> emptyList()
            }
        }

    private val categorySlideshowInterval = navArea
        .filter { it == NavigationArea.Category }
        .flatMapLatest { photoPreferenceRepository.getSlideshowIntervalSeconds() }

    private val randomSlideshowInterval = navArea
        .filter { it == NavigationArea.Random }
        .flatMapLatest { randomPreferenceRepository.getSlideshowIntervalSeconds() }

    val photos = merge(randomPhotos, categoryPhotos)
    val slideshowInterval = merge(categorySlideshowInterval, randomSlideshowInterval)

    val activePhotoIndex = isPhotoScreen
        .flatMapLatest { activeIdRepository.getActivePhotoId() }
        .filter { it != null }
        .combine(photos) { id, photos -> photos.indexOfFirst { it.id == id } }

    val activePhoto = isPhotoScreen
        .flatMapLatest { activePhotoIndex }
        .filter { it >= 0 }
        .combine(photos) { index, photos -> if(photos.size > index) photos[index] else null }

    val activeCategory = activePhoto
        .filter { it != null }
        .flatMapLatest { photoCategoryRepository.getCategory(it!!.categoryId) }
}