package us.mikeandwan.photos.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryListRecyclerAdapter
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridFragment
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItemWithSize
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridRecyclerAdapter
import us.mikeandwan.photos.ui.controls.yearnavmenu.YearListRecyclerAdapter
import us.mikeandwan.photos.utils.GlideApp
import java.io.File


@BindingAdapter("yearListData")
fun bindYearRecyclerView(recyclerView: RecyclerView, data: List<Int>?) {
    val adapter = recyclerView.adapter as YearListRecyclerAdapter
    adapter.submitList(data)
}

@BindingAdapter("categoryList")
fun bindCategoryListRecyclerView(recyclerView: RecyclerView, data: List<PhotoCategory>?) {
    when(val adapter = recyclerView.adapter) {
        is CategoryListRecyclerAdapter -> adapter.submitList(data)
    }
}

@BindingAdapter("imageGridItemList")
fun bindImageGridRecyclerView(recyclerView: RecyclerView, gridItems: List<ImageGridItemWithSize>?) {
    when(val adapter = recyclerView.adapter) {
        is ImageGridRecyclerAdapter -> adapter.submitList(gridItems)
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
        imgView.setColorFilter(color)
    }
}

@BindingAdapter("imageGridThumbnailSize")
fun bindImageGridThumbnailSize(container: FragmentContainerView, thumbnailSize: GridThumbnailSize) {
    val imageGridFragment = container.getFragment<ImageGridFragment>()

    imageGridFragment.setThumbnailSize(thumbnailSize)
}

@BindingAdapter("imageGridPhotoList")
fun bindImageGridPhotoList(container: FragmentContainerView, photoList: List<Photo>) {
    val imageGridFragment = container.getFragment<ImageGridFragment>()

    imageGridFragment.setGridItems(photoList.map { it -> it.toImageGridItem() })
}

@BindingAdapter("imageGridFileList")
fun bindImageGridFileList(container: FragmentContainerView, fileList: List<File>) {
    val imageGridFragment = container.getFragment<ImageGridFragment>()

    imageGridFragment.setGridItems(fileList.mapIndexed { id, file -> ImageGridItem(id, file.path, file) })
}

@BindingAdapter("imageGridClickHandler")
fun bindImageGridClickHandler(container: FragmentContainerView, handler: ImageGridRecyclerAdapter.ClickListener) {
    val imageGridFragment = container.getFragment<ImageGridFragment>()

    imageGridFragment.setClickHandler(handler)
}

@BindingAdapter("markdownText")
fun bindMarkdownText(textView: TextView, markdown: String) {
    val markwon = Markwon.create(textView.context)

    markwon.setMarkdown(textView, markdown)
}