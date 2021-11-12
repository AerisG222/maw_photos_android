package us.mikeandwan.photos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorizationDao {
    @Query("SELECT * FROM authorization WHERE id = :id")
    fun getAuthorization(id: Int): Flow<Authorization?>

    @Query("DELETE FROM authorization WHERE id = :id")
    suspend fun deleteAuthorization(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAuthorization(authorization: Authorization)
}