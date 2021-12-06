package us.mikeandwan.photos.ui.controls.categorylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.databinding.ViewHolderCategoryListItemBinding
import us.mikeandwan.photos.domain.models.PhotoCategory

class CategoryListRecyclerAdapter(private val clickListener: ClickListener)
    : ListAdapter<CategoryWithYearVisibility, CategoryListRecyclerAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHolderCategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryWithYearVisibility = getItem(position)

        holder.bind(categoryWithYearVisibility, clickListener)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CategoryWithYearVisibility>() {
        override fun areItemsTheSame(oldItem: CategoryWithYearVisibility, newItem: CategoryWithYearVisibility): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CategoryWithYearVisibility, newItem: CategoryWithYearVisibility): Boolean {
            return oldItem.category.id == newItem.category.id
        }
    }

    class ViewHolder(private var binding: ViewHolderCategoryListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryWithYearVisibility: CategoryWithYearVisibility, clickListener: ClickListener) {
            binding.viewModel = categoryWithYearVisibility
            binding.root.setOnClickListener { clickListener.onClick(categoryWithYearVisibility.category) }
            binding.executePendingBindings()
        }
    }

    class ClickListener(val clickListener: (category: PhotoCategory) -> Unit) {
        fun onClick(category: PhotoCategory) = clickListener(category)
    }
}
