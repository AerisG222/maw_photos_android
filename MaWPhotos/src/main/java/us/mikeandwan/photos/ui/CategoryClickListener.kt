package us.mikeandwan.photos.ui

import us.mikeandwan.photos.domain.models.PhotoCategory

class CategoryClickListener(val clickListener: (category: PhotoCategory) -> Unit) {
    fun onClick(category: PhotoCategory) = clickListener(category)
}