package us.mikeandwan.photos.ui.controls.yearnavmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.StateFlow
import us.mikeandwan.photos.databinding.ViewHolderYearListItemBinding
import us.mikeandwan.photos.utils.getTextColor


class YearListRecyclerAdapter(private val activeYear: StateFlow<Int?>, private val clickListener: ClickListener)
    : ListAdapter<Int, YearListRecyclerAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHolderYearListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val year = getItem(position)

        holder.bind(year, activeYear, clickListener)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(private var binding: ViewHolderYearListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(year: Int, activeYear: StateFlow<Int?>, clickListener: ClickListener) {
            binding.year = year
            binding.color = getColor(year, activeYear)
            binding.yearTextView.setOnClickListener { clickListener.onClick(year) }
            binding.executePendingBindings()
        }

        private fun getColor(year: Int, activeYear: StateFlow<Int?>): Int {
            return this.itemView.context.getTextColor(year == activeYear.value)
        }
    }

    class ClickListener(val clickListener: (year: Int) -> Unit) {
        fun onClick(year: Int) = clickListener(year)
    }
}
