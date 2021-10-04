package us.mikeandwan.photos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PhotoCategoryDao {
    @Query("SELECT DISTINCT year FROM photo_category ORDER BY year DESC")
    abstract fun getYears(): Flow<List<Int>>

    @Query("""
        SELECT pc.*
          FROM photo_category pc
         INNER JOIN active_id ai
                 ON ai.type = :type
                AND ai.id = pc.year
         ORDER BY id DESC
    """
    )
    protected abstract fun getCategoriesForActiveYear(type: ActiveIdType): Flow<List<PhotoCategory>>

    @Query("""
        SELECT pc.*
          FROM photo_category pc
         INNER JOIN active_id ai
                 ON ai.type = :type
                AND ai.id = pc.id
    """)
    abstract fun getActiveCategory(type: ActiveIdType): Flow<PhotoCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(vararg categories: PhotoCategory)

    fun getCategoriesForActiveYear(): Flow<List<PhotoCategory>> = getCategoriesForActiveYear(ActiveIdType.PhotoCategoryYear)
    fun getActiveCategory(): Flow<PhotoCategory> = getActiveCategory(ActiveIdType.PhotoCategory)
}