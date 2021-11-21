package us.mikeandwan.photos.ui.controls.imagegrid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import us.mikeandwan.photos.databinding.ViewHolderImageGridItemBinding

class ImageGridRecyclerAdapter(private val clickListener: ClickListener)
    : ListAdapter<ImageGridItemWithSize, ImageGridRecyclerAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHolderImageGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, clickListener)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ImageGridItemWithSize>() {
        override fun areItemsTheSame(oldItem: ImageGridItemWithSize, newItem: ImageGridItemWithSize): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ImageGridItemWithSize, newItem: ImageGridItemWithSize): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ViewHolder(private var binding: ViewHolderImageGridItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageGridItemWithSize, clickListener: ClickListener) {
            binding.item = item
            binding.root.setOnClickListener { clickListener.onClick(item) }
            binding.imageGridItemImageView.layoutParams.height = item.size
            binding.imageGridItemImageView.layoutParams.width = item.size

            val layoutParams = binding.root.layoutParams

            if(layoutParams is FlexboxLayoutManager.LayoutParams) {
                layoutParams.flexGrow = 1f
            }

            binding.executePendingBindings()
        }
    }

    class ClickListener(val clickListener: (item: ImageGridItemWithSize) -> Unit) {
        fun onClick(item: ImageGridItemWithSize) = clickListener(item)
    }
}
