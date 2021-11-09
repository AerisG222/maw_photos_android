package us.mikeandwan.photos.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class PhotoListMediator @Inject constructor (
    activeIdRepository: ActiveIdRepository,
    navigationStateRepository: NavigationStateRepository,
    photoCategoryRepository: PhotoCategoryRepository,
    randomPhotoRepository: RandomPhotoRepository
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

    val photos = merge(randomPhotos, categoryPhotos)

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