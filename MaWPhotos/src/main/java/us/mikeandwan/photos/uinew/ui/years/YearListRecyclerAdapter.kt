package us.mikeandwan.photos.uinew.ui.years

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.databinding.YearListItemViewHolderBinding

class YearListRecyclerAdapter(val clickListener: ClickListener)
    : ListAdapter<Int, YearListRecyclerAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var binding: YearListItemViewHolderBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(year: Int) {
            binding.year = year
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = YearListItemViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val year = getItem(position)

        holder.itemView.setOnClickListener {
            clickListener.onClick(year)
        }

        holder.bind(year)
    }

    class ClickListener(val clickListener: (year: Int) -> Unit) {
        fun onClick(year: Int) = clickListener(year)
    }
}
