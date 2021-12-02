package us.mikeandwan.photos.ui.controls.searchnavmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.databinding.ViewHolderSearchTermListItemBinding


class SearchTermListRecyclerAdapter(private val clickListener: ClickListener)
    : ListAdapter<String, SearchTermListRecyclerAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHolderSearchTermListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val term = getItem(position)

        holder.bind(term, clickListener)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(private var binding: ViewHolderSearchTermListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(term: String, clickListener: ClickListener) {
            binding.term = term
            binding.searchTermTextView.setOnClickListener { clickListener.onClick(term) }
            binding.executePendingBindings()
        }
    }

    class ClickListener(val clickListener: (term: String) -> Unit) {
        fun onClick(term: String) = clickListener(term)
    }
}
