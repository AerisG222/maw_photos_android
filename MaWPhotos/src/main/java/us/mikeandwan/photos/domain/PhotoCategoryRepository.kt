package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.PhotoCategoryDao
import javax.inject.Inject

class PhotoCategoryRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val dao: PhotoCategoryDao
) {
    fun getYears() = flow {
        val data = dao.getYears()

        if(data.first().isEmpty()) {
            emit(emptyList())
            loadCategories()
        }

        emitAll(data)
    }

    fun getCategories() = dao
        .getByYear(2021)
        .map { dbList ->
            dbList.map { dbCat -> dbCat.toDomainPhotoCategory() }
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