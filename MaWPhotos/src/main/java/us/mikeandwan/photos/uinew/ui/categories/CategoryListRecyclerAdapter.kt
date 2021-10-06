package us.mikeandwan.photos.uinew.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.databinding.CategoryListItemViewHolderBinding
import us.mikeandwan.photos.domain.PhotoCategory

class CategoryListRecyclerAdapter(private val clickListener: ClickListener)
    : CategoryRecyclerAdapter<CategoryListRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoryListItemViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)

        holder.bind(category, clickListener)
    }

    class ViewHolder(private var binding: CategoryListItemViewHolderBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(photoCategory: PhotoCategory, clickListener: CategoryRecyclerAdapter.ClickListener) {
            binding.category = photoCategory
            binding.root.setOnClickListener { clickListener.onClick(photoCategory) }
            binding.executePendingBindings()
        }
    }
}
