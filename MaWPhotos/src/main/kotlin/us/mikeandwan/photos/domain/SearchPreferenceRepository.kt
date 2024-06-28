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
    companion object {
        private const val PREFERENCE_ID = 1
    }

    fun getSearchesToSaveCount() = dao
        .getSearchPreference(PREFERENCE_ID)
        .map { it.recentQueryCount }

    fun getSearchGridItemSize() = dao
        .getSearchPreference(PREFERENCE_ID)
        .map { it.gridThumbnailSize }

    fun getSearchDisplayType() = dao
        .getSearchPreference(PREFERENCE_ID)
        .map { it.displayType }

    suspend fun setSearchesToSaveCount(count: Int) {
        setPreference { it.copy(recentQueryCountToSave = count) }
    }

    suspend fun setSearchDisplayType(mode: CategoryDisplayType) {
        setPreference { it.copy(displayType = mode) }
    }

    suspend fun setSearchGridItemSize(size: GridThumbnailSize) {
        setPreference { it.copy(gridThumbnailSize = size) }
    }

    private fun getSearchPreferences() = dao
        .getSearchPreference(PREFERENCE_ID)
        .map { it.toDomainSearchPreference() }

    private suspend fun setSearchPreferences(pref: SearchPreference) {
        val dbPref = us.mikeandwan.photos.database.SearchPreference(
            PREFERENCE_ID,
            pref.recentQueryCountToSave,
            pref.displayType,
            pref.gridThumbnailSize)

        dao.setSearchPreference(dbPref)
    }

    private suspend fun setPreference(update: (pref: SearchPreference) -> SearchPreference) {
        val pref = getSearchPreferences().first()

        setSearchPreferences(update(pref))
    }
}
