package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.CategoryPreference
import us.mikeandwan.photos.database.CategoryPreferenceDao
import javax.inject.Inject

class CategoryPreferenceRepository @Inject constructor(
    private val dao: CategoryPreferenceDao
) {
    fun getCategoryDisplayType() = dao
        .getCategoryPreference(Constants.ID)
        .map { it.displayType }

    suspend fun setCategoryDisplayType(displayType: CategoryDisplayType) = dao
        .setCategoryPreference(CategoryPreference(Constants.ID, displayType))

    object Constants {
        const val ID = 1
    }
}