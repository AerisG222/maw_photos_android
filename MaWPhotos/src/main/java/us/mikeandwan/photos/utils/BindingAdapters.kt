package us.mikeandwan.photos.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.PhotoCategory
import us.mikeandwan.photos.uinew.ui.categories.CategoryGridRecyclerAdapter
import us.mikeandwan.photos.uinew.ui.categories.CategoryListRecyclerAdapter
import us.mikeandwan.photos.uinew.ui.years.YearListRecyclerAdapter

@BindingAdapter("yearListData")
fun bindYearRecyclerView(recyclerView: RecyclerView, data: List<Int>?) {
    val adapter = recyclerView.adapter as YearListRecyclerAdapter
    adapter.submitList(data)
}

@BindingAdapter("listData")
fun bindCategoryRecyclerView(recyclerView: RecyclerView, data: List<PhotoCategory>?) {
    when(val adapter = recyclerView.adapter) {
        is CategoryGridRecyclerAdapter -> adapter.submitList(data)
        is CategoryListRecyclerAdapter -> adapter.submitList(data)
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Glide.with(imgView.context)
            .load(imgUrl)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
}