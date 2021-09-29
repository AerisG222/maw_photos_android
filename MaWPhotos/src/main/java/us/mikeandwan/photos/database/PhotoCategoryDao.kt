package us.mikeandwan.photos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoCategoryDao {
    @Query("SELECT DISTINCT year FROM photo_category ORDER BY year DESC")
    suspend fun getYears(): Flow<List<Int>>

    @Query("SELECT * FROM photo_category WHERE year = :year ORDER BY id DESC")
    suspend fun getByYear(year: Int): Flow<List<PhotoCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg categories: PhotoCategory)
}