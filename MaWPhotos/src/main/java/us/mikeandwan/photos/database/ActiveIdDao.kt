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
    abstract suspend fun setActivePhotoCategoryYear(year: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun setActivePhotoCategory(id: Int)
}