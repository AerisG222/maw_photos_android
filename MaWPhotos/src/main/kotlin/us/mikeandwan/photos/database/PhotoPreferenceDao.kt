package us.mikeandwan.photos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoPreferenceDao {
    @Query("SELECT * FROM photo_preference WHERE id = :id")
    fun getPhotoPreference(id: Int): Flow<PhotoPreference>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPhotoPreference(preference: PhotoPreference)
}