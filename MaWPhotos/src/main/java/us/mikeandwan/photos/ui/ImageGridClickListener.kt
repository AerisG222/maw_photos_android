package us.mikeandwan.photos.ui

import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItemWithSize

class ImageGridClickListener(val clickListener: (item: ImageGridItemWithSize) -> Unit) {
    fun onClick(item: ImageGridItemWithSize) = clickListener(item)
}
