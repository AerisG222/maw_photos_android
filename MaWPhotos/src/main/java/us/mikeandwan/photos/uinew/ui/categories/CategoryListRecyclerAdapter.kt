package us.mikeandwan.photos.uinew.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.databinding.CategoryListItemViewHolderBinding
import us.mikeandwan.photos.domain.PhotoCategory

class CategoryListRecyclerAdapter(val clickListener: PhotoCategoryClickListener)
    : ListAdapter<PhotoCategory, CategoryListRecyclerAdapter.CategoryListViewHolder>(DiffCallback) {

    class CategoryListViewHolder(private var binding: CategoryListItemViewHolderBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(photoCategory: PhotoCategory) {
            binding.category = photoCategory
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PhotoCategory>() {
        override fun areItemsTheSame(oldItem: PhotoCategory, newItem: PhotoCategory): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoCategory, newItem: PhotoCategory): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding = CategoryListItemViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CategoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val category = getItem(position)

        holder.itemView.setOnClickListener {
            clickListener.onClick(category)
        }

        holder.bind(category)
    }

    class PhotoCategoryClickListener(val clickListener: (category: PhotoCategory) -> Unit) {
        fun onClick(category: PhotoCategory) = clickListener(category)
    }
}
