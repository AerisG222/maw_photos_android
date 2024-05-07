package us.mikeandwan.photos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PhotoCategoryDao {
    @Query("SELECT MAX(year) FROM photo_category")
    abstract fun getMostRecentYear(): Flow<Int?>

    @Query("SELECT DISTINCT year FROM photo_category ORDER BY year DESC")
    abstract fun getYears(): Flow<List<Int>>

    @Query("""
        SELECT pc.*
          FROM photo_category pc
         WHERE pc.year = :year
         ORDER BY pc.id DESC
    """
    )
    abstract fun getCategoriesForYear(year: Int): Flow<List<PhotoCategory>>

    @Query("""
        SELECT pc.*
          FROM photo_category pc
         WHERE id = :id
    """)
    abstract fun getCategory(id: Int): Flow<PhotoCategory?>

    @Query("""
        SELECT pc.*
          FROM photo_category pc
         WHERE pc.id = (SELECT MAX(id) FROM photo_category)
    """)
    abstract fun getMostRecentCategory(): Flow<PhotoCategory?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(vararg categories: PhotoCategory)
}
