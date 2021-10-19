package us.mikeandwan.photos.ui.categories

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import us.mikeandwan.photos.R
import us.mikeandwan.photos.models.Category
import us.mikeandwan.photos.services.DataServices

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
        return _dataServices.downloadMdCategoryTeaser(category!!)
    }

    override fun displayCategory(
        category: Category?,
        imageFile: String?,
        viewHolder: ViewHolder?
    ) {
        Picasso
            .get()
            .load(imageFile)
            .resizeDimen(R.dimen.image_grid_thumbnail_size_medium, R.dimen.image_grid_thumbnail_size_medium)
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