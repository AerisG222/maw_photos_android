package us.mikeandwan.photos.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_preference")
data class PhotoPreference(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "display_toolbar") val displayToolbar: Boolean,
    @ColumnInfo(name = "display_thumbnails") val displayThumbnails: Boolean,
    @ColumnInfo(name = "display_top_toolbar") val displayTopToolbar: Boolean,
    @ColumnInfo(name = "fade_controls") val doFadeControls: Boolean,
    @ColumnInfo(name = "slideshow_interval_seconds") val slideshowIntervalSeconds: Int
)