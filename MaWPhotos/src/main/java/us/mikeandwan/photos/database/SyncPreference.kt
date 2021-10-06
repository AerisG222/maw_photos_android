package us.mikeandwan.photos.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_preference")
data class SyncPreference (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "sync_frequency_hours") val syncFrequencyHours: Int
)
