package us.mikeandwan.photos.uinew.ui.categories

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.domain.PhotoCategory

abstract class CategoryRecyclerAdapter<T : RecyclerView.ViewHolder?>
    : ListAdapter<PhotoCategory, T>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<PhotoCategory>() {
        override fun areItemsTheSame(oldItem: PhotoCategory, newItem: PhotoCategory): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoCategory, newItem: PhotoCategory): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ClickListener(val clickListener: (category: PhotoCategory) -> Unit) {
        fun onClick(category: PhotoCategory) = clickListener(category)
    }
}