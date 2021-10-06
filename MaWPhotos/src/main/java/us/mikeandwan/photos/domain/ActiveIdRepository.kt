package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.Flow
import us.mikeandwan.photos.database.ActiveId
import us.mikeandwan.photos.database.ActiveIdDao
import us.mikeandwan.photos.database.ActiveIdType
import javax.inject.Inject

class ActiveIdRepository @Inject constructor(
    private val dao: ActiveIdDao
) {
    fun getActivePhotoCategoryYear(): Flow<Int> = dao.getActiveId(ActiveIdType.PhotoCategoryYear)
    fun getActivePhotoCategoryId(): Flow<Int> = dao.getActiveId(ActiveIdType.PhotoCategory)

    suspend fun setActivePhotoCategoryYear(year: Int) {
        val id = ActiveId(ActiveIdType.PhotoCategoryYear, year)

        dao.setActiveId(id)
    }

    suspend fun setActivePhotoCategory(categoryId: Int) {
        val id = ActiveId(ActiveIdType.PhotoCategory, categoryId)

        dao.setActiveId(id)
    }
}