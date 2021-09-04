package us.mikeandwan.photos.ui.categories

import us.mikeandwan.photos.services.DataServices
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import us.mikeandwan.photos.R
import com.squareup.picasso.Picasso
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import us.mikeandwan.photos.models.Category

class ListCategoryRecyclerAdapter(
    activity: ICategoryListActivity?,
    dataServices: DataServices?
) : CategoryRecyclerAdapter<ListCategoryRecyclerAdapter.ViewHolder?>(
    activity!!, dataServices!!
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val categoryView =
            LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return ViewHolder(categoryView)
    }

    override fun downloadCategoryTeaser(category: Category?): String? {
        return _dataServices.downloadCategoryTeaser(category)
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

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        @JvmField
        @BindView(R.id.thumbnailImageView)
        var _thumbnailImageView: ImageView? = null

        @JvmField
        @BindView(R.id.categoryNameTextView)
        var _nameTextView: TextView? = null

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }
}