package us.mikeandwan.photos.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ActiveIdDao {
    @Query("SELECT id FROM active_id WHERE type = :type")
    protected abstract fun getActiveId(type: ActiveIdType): Flow<Int>

    fun getActivePhotoCategoryYear(): Flow<Int> = getActiveId(ActiveIdType.PhotoCategoryYear)
    fun getActivePhotoCategoryId(): Flow<Int> = getActiveId(ActiveIdType.PhotoCategory)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun setActiveId(activeId: ActiveId)

    suspend fun setActivePhotoCategoryYear(year: Int) {
        val id = ActiveId(ActiveIdType.PhotoCategoryYear, year)

        setActiveId(id)
    }

    suspend fun setActivePhotoCategory(categoryId: Int) {
        val id = ActiveId(ActiveIdType.PhotoCategory, categoryId)

        setActiveId(id)
    }
}