package us.mikeandwan.photos.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.PhotoCategory
import us.mikeandwan.photos.uinew.ui.categories.CategoryListRecyclerAdapter
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridItem
import us.mikeandwan.photos.uinew.ui.imageGrid.ImageGridRecyclerAdapter
import us.mikeandwan.photos.uinew.ui.years.YearListRecyclerAdapter

@BindingAdapter("yearListData")
fun bindYearRecyclerView(recyclerView: RecyclerView, data: List<Int>?) {
    val adapter = recyclerView.adapter as YearListRecyclerAdapter
    adapter.submitList(data)
}

@BindingAdapter("listData")
fun bindCategoryRecyclerView(recyclerView: RecyclerView, data: List<PhotoCategory>?) {
    when(val adapter = recyclerView.adapter) {
        is CategoryListRecyclerAdapter -> adapter.submitList(data)
    }
}

@BindingAdapter("imageGridItemList")
fun bindImageGridRecyclerView(recyclerView: RecyclerView, data: List<ImageGridItem>?) {
    when(val adapter = recyclerView.adapter) {
        is ImageGridRecyclerAdapter -> adapter.submitList(data)
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        GlideApp.with(imgView.context)
            .load(imgUrl)
            .centerCrop()
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(imgView)
    }
}

@BindingAdapter("tint")
fun bindTint(imgView: ImageView, color: Int?) {
    color?.let {
        imgView.setColorFilter(imgView.context.getColor(color))
    }
}
