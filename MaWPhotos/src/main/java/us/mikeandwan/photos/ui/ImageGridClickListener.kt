package us.mikeandwan.photos.ui

import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem

class ImageGridClickListener(val clickListener: (item: ImageGridItem) -> Unit) {
    fun onClick(item: ImageGridItem) = clickListener(item)
}
