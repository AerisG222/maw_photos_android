package us.mikeandwan.photos.ui.controls.imagegrid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import us.mikeandwan.photos.databinding.ViewHolderImageGridItemBinding

class ImageGridRecyclerAdapter(private val thumbnailSize: Int, private val clickListener: ClickListener)
    : ListAdapter<ImageGridItem, ImageGridRecyclerAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHolderImageGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding, thumbnailSize)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, clickListener)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ImageGridItem>() {
        override fun areItemsTheSame(oldItem: ImageGridItem, newItem: ImageGridItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ImageGridItem, newItem: ImageGridItem): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ViewHolder(private var binding: ViewHolderImageGridItemBinding, private val thumbnailSize: Int)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageGridItem, clickListener: ClickListener) {
            binding.item = item
            binding.imageGridItemImageView.layoutParams.height = thumbnailSize
            binding.imageGridItemImageView.layoutParams.width = thumbnailSize

            binding.root.setOnClickListener { clickListener.onClick(item) }

            val layoutParams = binding.root.layoutParams

            if(layoutParams is FlexboxLayoutManager.LayoutParams) {
                layoutParams.flexGrow = 1f
            }

            binding.executePendingBindings()
        }
    }

    class ClickListener(val clickListener: (item: ImageGridItem) -> Unit) {
        fun onClick(item: ImageGridItem) = clickListener(item)
    }
}
