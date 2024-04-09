package us.mikeandwan.photos.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_id")
data class ActiveId(
    @PrimaryKey val type: ActiveIdType,
    val id: Int
)
