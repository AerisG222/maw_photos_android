package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.SearchPreferenceDao
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.SearchPreference
import javax.inject.Inject

class SearchPreferenceRepository @Inject constructor (
    private val dao: SearchPreferenceDao
) {
    fun getSearchPreferences() = dao
        .getSearchPreference(Constants.ID)
        .map { it.toDomainSearchPreference() }

    fun getRecentSearchesToSaveCount() = dao
        .getSearchPreference(Constants.ID)
        .map { it.recentQueryCount }

    fun getGridThumbnailSize() = dao
        .getSearchPreference(Constants.ID)
        .map { it.gridThumbnailSize }

    fun getDisplayMode() = dao
        .getSearchPreference(Constants.ID)
        .map { it.displayType }

    suspend fun setSearchesToSaveCount(count: Int) {
        setPreference { it.copy(recentQueryCountToSave = count) }
    }

    suspend fun setDisplayMode(mode: CategoryDisplayType) {
        setPreference { it.copy(displayType = mode) }
    }

    suspend fun setGridItemSize(size: GridThumbnailSize) {
        setPreference { it.copy(gridThumbnailSize = size) }
    }

    private suspend fun setSearchPreferences(pref: SearchPreference) {
        val dbPref = us.mikeandwan.photos.database.SearchPreference(
            Constants.ID,
            pref.recentQueryCountToSave,
            pref.displayType,
            pref.gridThumbnailSize)

        dao.setSearchPreference(dbPref)
    }

    private suspend fun setPreference(update: (pref: SearchPreference) -> SearchPreference) {
        val pref = getSearchPreferences().first()

        setSearchPreferences(update(pref))
    }

    object Constants {
        const val ID = 1
    }
}