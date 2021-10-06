package us.mikeandwan.photos.uinew.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.databinding.CategoryGridItemViewHolderBinding
import us.mikeandwan.photos.domain.PhotoCategory

class CategoryGridRecyclerAdapter(private val clickListener: ClickListener)
    : CategoryRecyclerAdapter<CategoryGridRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoryGridItemViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)

        holder.bind(category, clickListener)
    }

    class ViewHolder(private var binding: CategoryGridItemViewHolderBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(photoCategory: PhotoCategory, clickListener: ClickListener) {
            binding.category = photoCategory
            binding.root.setOnClickListener { clickListener.onClick(photoCategory) }
            binding.executePendingBindings()
        }
    }
}
