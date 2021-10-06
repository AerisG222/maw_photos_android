package us.mikeandwan.photos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncPreferenceDao {
    @Query("SELECT * FROM sync_preference WHERE id = :id")
    fun getSyncPreference(id: Int): Flow<SyncPreference>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSyncPreference(preference: SyncPreference)
}