package us.mikeandwan.photos.ui.categories

import android.view.View
import us.mikeandwan.photos.ui.categories.ICategoryListActivity
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.ui.categories.CategoryRecyclerAdapter
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import us.mikeandwan.photos.R
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.models.Category

class ThumbnailCategoryRecyclerAdapter(
    activity: ICategoryListActivity?,
    dataServices: DataServices?
) : CategoryRecyclerAdapter<ThumbnailCategoryRecyclerAdapter.ViewHolder?>(
    activity!!, dataServices!!
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context)
        imageView.setPadding(4, 4, 4, 4)
        imageView.adjustViewBounds = true
        return ViewHolder(imageView)
    }

    override fun downloadCategoryTeaser(category: Category?): String? {
        return _dataServices.downloadMdCategoryTeaser(category)
    }

    protected override fun displayCategory(
        category: Category?,
        path: String?,
        viewHolder: ViewHolder?
    ) {
        Picasso
            .get()
            .load(path)
            .resizeDimen(R.dimen.category_grid_thumbnail_size, R.dimen.category_grid_thumbnail_size)
            .centerCrop()
            .into(viewHolder!!._thumbnailImageView)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var _thumbnailImageView: ImageView?

        init {
            _thumbnailImageView = itemView as ImageView?
        }
    }
}