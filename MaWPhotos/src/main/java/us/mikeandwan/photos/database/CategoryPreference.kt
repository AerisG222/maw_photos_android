package us.mikeandwan.photos.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import us.mikeandwan.photos.domain.CategoryDisplayType

@Entity(tableName = "category_preference")
data class CategoryPreference(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "display_type") val displayType: CategoryDisplayType
)