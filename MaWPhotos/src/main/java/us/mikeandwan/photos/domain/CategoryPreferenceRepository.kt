package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.CategoryPreferenceDao
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

    suspend fun setCategoryPreference(pref: CategoryPreference) = dao
        .setCategoryPreference(us.mikeandwan.photos.database.CategoryPreference(Constants.ID, pref.displayType))

    object Constants {
        const val ID = 1
    }
}