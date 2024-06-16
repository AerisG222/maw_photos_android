package us.mikeandwan.photos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VideoCategoryDao {
    @Query("SELECT MAX(year) FROM video_category")
    abstract fun getMostRecentYear(): Flow<Int?>

    @Query("SELECT DISTINCT year FROM video_category ORDER BY year DESC")
    abstract fun getYears(): Flow<List<Int>>

    @Query("""
        SELECT vc.*
          FROM video_category vc
         WHERE vc.year = :year
         ORDER BY vc.id DESC
    """
    )
    abstract fun getCategoriesForYear(year: Int): Flow<List<VideoCategory>>

    @Query("""
        SELECT vc.*
          FROM video_category vc
         WHERE id = :id
    """)
    abstract fun getCategory(id: Int): Flow<VideoCategory?>

    @Query("""
        SELECT vc.*
          FROM video_category vc
         WHERE vc.id = (SELECT MAX(id) FROM video_category)
    """)
    abstract fun getMostRecentCategory(): Flow<VideoCategory?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(vararg categories: VideoCategory)
}
