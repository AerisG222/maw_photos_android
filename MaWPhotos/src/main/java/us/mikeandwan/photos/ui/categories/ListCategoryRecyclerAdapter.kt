package us.mikeandwan.photos.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.CategoryListItemBinding
import us.mikeandwan.photos.models.Category
import us.mikeandwan.photos.services.DataServices

class ListCategoryRecyclerAdapter(
    activity: ICategoryListActivity?,
    dataServices: DataServices?
) : CategoryRecyclerAdapter<ListCategoryRecyclerAdapter.ViewHolder?>(
    activity!!, dataServices!!
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun downloadCategoryTeaser(category: Category?): String? {
        return _dataServices.downloadCategoryTeaser(category!!)
    }

    override fun displayCategory(
        category: Category?,
        path: String?,
        viewHolder: ViewHolder?
    ) {
        viewHolder!!._nameTextView!!.text = category!!.name
        Picasso
            .get()
            .load(path)
            .resizeDimen(R.dimen.category_list_thumbnail_size, R.dimen.category_list_thumbnail_size)
            .centerCrop()
            .into(viewHolder._thumbnailImageView)
    }

    class ViewHolder(private val binding: CategoryListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var _thumbnailImageView = binding.thumbnailImageView
        var _nameTextView = binding.categoryNameTextView
    }
}