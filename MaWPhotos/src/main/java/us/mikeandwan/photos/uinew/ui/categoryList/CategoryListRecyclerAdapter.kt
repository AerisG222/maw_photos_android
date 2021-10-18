package us.mikeandwan.photos.uinew.ui.categoryList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.databinding.ViewHolderCategoryListItemBinding
import us.mikeandwan.photos.domain.PhotoCategory

class CategoryListRecyclerAdapter(private val clickListener: ClickListener)
    : ListAdapter<PhotoCategory, CategoryListRecyclerAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHolderCategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)

        holder.bind(category, clickListener)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PhotoCategory>() {
        override fun areItemsTheSame(oldItem: PhotoCategory, newItem: PhotoCategory): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoCategory, newItem: PhotoCategory): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ViewHolder(private var binding: ViewHolderCategoryListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(photoCategory: PhotoCategory, clickListener: CategoryListRecyclerAdapter.ClickListener) {
            binding.category = photoCategory
            binding.root.setOnClickListener { clickListener.onClick(photoCategory) }
            binding.executePendingBindings()
        }
    }

    class ClickListener(val clickListener: (category: PhotoCategory) -> Unit) {
        fun onClick(category: PhotoCategory) = clickListener(category)
    }
}
