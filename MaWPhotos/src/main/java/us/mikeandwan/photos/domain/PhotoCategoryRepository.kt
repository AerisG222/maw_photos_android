package us.mikeandwan.photos.domain

import androidx.room.withTransaction
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.database.*
import javax.inject.Inject
import kotlin.math.max

class PhotoCategoryRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val db: MawDatabase,
    private val pcDao: PhotoCategoryDao,
    private val idDao: ActiveIdDao
) {
    fun getYears() = flow {
        val data = pcDao.getYears()

        if(data.first().isEmpty()) {
            emit(emptyList())
            loadCategories()
        }

        emitAll(data)
    }

    fun getCategories() = pcDao
        .getCategoriesForActiveYear()
        .map { dbList ->
            dbList.map { dbCat -> dbCat.toDomainPhotoCategory() }
        }

    fun getCategory() = pcDao
        .getActiveCategory()
        .map { cat -> cat.toDomainPhotoCategory() }

    suspend fun getPhotos(categoryId: Int): List<Photo> {
        val result = api.getPhotos(categoryId)

        return result?.items?.map{ it.toDomainPhoto() } ?: emptyList()
    }

    private suspend fun loadCategories() {
        val categories = api.getRecentCategories(-1)

        if(categories == null || categories.count == 0L) {
            // handle error
            return
        }

        val dbCategories = categories.items
            .map { apiCat -> apiCat.toDatabasePhotoCategory() }

        val maxYear = dbCategories.maxOf { it.year }

        db.withTransaction {
            pcDao.upsert(*dbCategories.toTypedArray())
            idDao.setActiveId(ActiveId(ActiveIdType.PhotoCategoryYear, maxYear))
        }
    }
}