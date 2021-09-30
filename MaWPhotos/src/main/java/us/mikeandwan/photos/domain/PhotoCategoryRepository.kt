package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.PhotoCategoryDao
import javax.inject.Inject

class PhotoCategoryRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val dao: PhotoCategoryDao
) {
    suspend fun getCategories(): Flow<List<PhotoCategory>> {
        val categoryFlow = dao
            .getByYear(2021)
            .map { dbList ->
                dbList.map { dbCat -> dbCat.toDomainPhotoCategory() }
            }

        categoryFlow
            .filter { it.isEmpty() }
            .map { loadCategories() }
            .collect()

        return categoryFlow
    }

    private suspend fun loadCategories() {
        val categories = api.getRecentCategories(-1)

        if(categories == null || categories.count == 0L) {
            // handle error
            return
        }

        val dbCategories = categories.items
            .map { apiCat -> apiCat.toDatabasePhotoCategory() }

        dao.upsert(*dbCategories.toTypedArray())
    }
}