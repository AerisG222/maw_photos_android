package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.CategoryPreferenceDao
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.CategoryPreference
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import javax.inject.Inject

class CategoryPreferenceRepository @Inject constructor(
    private val dao: CategoryPreferenceDao
) {
    fun getCategoryPreference() = dao
        .getCategoryPreference(Constants.ID)
        .map { it.toDomainCategoryPreference() }

    fun getCategoryDisplayType() = dao
        .getCategoryPreference(Constants.ID)
        .map { it.displayType }

    fun getCategoryGridItemSize() = dao
        .getCategoryPreference(Constants.ID)
        .map { it.gridThumbnailSize}

    suspend fun setCategoryDisplayType(displayType: CategoryDisplayType) {
        setPreference { it.copy(displayType = displayType) }
    }

    suspend fun setCategoryGridItemSize(size: GridThumbnailSize) {
        setPreference { it.copy(gridThumbnailSize = size) }
    }

    private suspend fun setCategoryPreference(pref: CategoryPreference) {
        val dbPref = us.mikeandwan.photos.database.CategoryPreference(
            Constants.ID,
            pref.displayType,
            pref.gridThumbnailSize)

        dao.setCategoryPreference(dbPref)
    }

    private suspend fun setPreference(update: (pref: CategoryPreference) -> CategoryPreference) {
        val pref = getCategoryPreference().first()

        setCategoryPreference(update(pref))
    }

    object Constants {
        const val ID = 1
    }
}