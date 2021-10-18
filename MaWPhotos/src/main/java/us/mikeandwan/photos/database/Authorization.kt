package us.mikeandwan.photos.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authorization")
data class Authorization(
    @PrimaryKey val id: Int,
    val json: String
)
