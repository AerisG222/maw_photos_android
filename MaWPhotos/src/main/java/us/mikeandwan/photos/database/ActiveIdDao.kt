package us.mikeandwan.photos.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveIdDao {
    @Query("SELECT id FROM active_id WHERE type = :type")
    fun getActiveId(type: ActiveIdType): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setActiveId(activeId: ActiveId)
}