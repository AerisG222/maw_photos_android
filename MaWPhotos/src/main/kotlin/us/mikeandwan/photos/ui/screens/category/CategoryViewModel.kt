package us.mikeandwan.photos.ui.screens.category

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import us.mikeandwan.photos.domain.MediaCategoryRepository
import us.mikeandwan.photos.domain.MediaPreferenceRepository
import us.mikeandwan.photos.domain.guards.AuthGuard
import us.mikeandwan.photos.domain.guards.CategoriesLoadedGuard
import us.mikeandwan.photos.domain.guards.GuardStatus
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.MediaCategory
import us.mikeandwan.photos.domain.models.MediaType
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import us.mikeandwan.photos.ui.toImageGridItem
import javax.inject.Inject

sealed class CategoryState {
    data object Loading: CategoryState()
    data object NotAuthorized: CategoryState()
    data object Error : CategoryState()
    data class Loaded(
        val category: MediaCategory,
        val gridItems: List<ImageGridItem<Media>>,
        val gridItemThumbnailSize: GridThumbnailSize
    ): CategoryState()
}

@HiltViewModel
class CategoryViewModel @Inject constructor (
    authGuard: AuthGuard,
    categoriesLoadedGuard: CategoriesLoadedGuard,
    mediaCategoryRepository: MediaCategoryRepository,
    mediaPreferenceRepository: MediaPreferenceRepository
) : BaseCategoryViewModel(
    mediaCategoryRepository
) {
    private val gridItems = media
        .map { items -> items.map { it.toImageGridItem() } }
        .stateIn(viewModelScope, WhileSubscribed(5000), emptyList())

    private val gridItemThumbnailSize = mediaPreferenceRepository
        .getPhotoGridItemSize()
        .stateIn(viewModelScope, WhileSubscribed(5000), GridThumbnailSize.Unspecified)

    fun initState(mediaType: String, categoryId: Int) {
        val type = MediaType.valueOf(mediaType)

        loadCategory(type, categoryId)
        loadMedia(type, categoryId)
    }

    val state = combine(
        authGuard.status,
        categoriesLoadedGuard.status,
        category,
        gridItems,
        gridItemThumbnailSize
    ) {
        authStatus,
        categoriesStatus,
        category,
        gridItems,
        gridItemThumbnailSize ->

        when(authStatus) {
            is GuardStatus.NotInitialized -> authGuard.initializeGuard()
            is GuardStatus.Failed -> CategoryState.NotAuthorized
            is GuardStatus.Passed ->
                when (categoriesStatus) {
                    is GuardStatus.NotInitialized -> categoriesLoadedGuard.initializeGuard()
                    is GuardStatus.Failed -> CategoryState.Error
                    is GuardStatus.Passed ->
                        if(category == null) {
                            CategoryState.Loading
                        } else {
                            CategoryState.Loaded(
                                category,
                                gridItems,
                                gridItemThumbnailSize
                            )
                        }
                }
        }
    }
    .stateIn(viewModelScope, WhileSubscribed(5000), CategoryState.Loading)
}
