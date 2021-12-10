package us.mikeandwan.photos.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.*
import us.mikeandwan.photos.ui.controls.categorychooser.CategoryChooserFragment
import us.mikeandwan.photos.ui.controls.categorylist.CategoryListRecyclerAdapter
import us.mikeandwan.photos.ui.controls.categorylist.CategoryWithYearVisibility
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridFragment
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItemWithSize
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridRecyclerAdapter
import us.mikeandwan.photos.ui.controls.searchnavmenu.SearchTermListRecyclerAdapter
import us.mikeandwan.photos.ui.controls.yearnavmenu.YearListRecyclerAdapter
import us.mikeandwan.photos.utils.GlideApp
import java.io.File


@BindingAdapter("yearListData")
fun bindYearRecyclerView(recyclerView: RecyclerView, data: List<Int>?) {
    val adapter = recyclerView.adapter as YearListRecyclerAdapter
    adapter.submitList(data)
}

@BindingAdapter("categoryList")
fun bindCategoryListRecyclerView(recyclerView: RecyclerView, data: List<CategoryWithYearVisibility>?) {
    when(val adapter = recyclerView.adapter) {
        is CategoryListRecyclerAdapter -> adapter.submitList(data)
    }
}

@BindingAdapter("searchTerms")
fun bindSearchTermRecyclerView(recyclerView: RecyclerView, data: List<SearchHistory>?) {
    val adapter = recyclerView.adapter as SearchTermListRecyclerAdapter
    adapter.submitList(data?.map{ it.term })
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

    imageGridFragment.setGridItems(photoList.map { it.toImageGridItem() })
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

@BindingAdapter("categoryChooserDisplayType")
fun bindCategoryChooserDisplayType(container: FragmentContainerView, displayType: CategoryDisplayType) {
    val categoryChooserFragment = container.getFragment<CategoryChooserFragment>()

    categoryChooserFragment.setDisplayType(displayType)
}

@BindingAdapter("categoryChooserCategories")
fun bindCategoryChooserCategories(container: FragmentContainerView, categories: List<PhotoCategory>) {
    val categoryChooserFragment = container.getFragment<CategoryChooserFragment>()

    categoryChooserFragment.setCategories(categories)
}

@BindingAdapter("categoryChooserCategorySelectedHandler")
fun bindCategoryChooserClickHandler(container: FragmentContainerView, handler: CategoryChooserFragment.CategorySelectedListener) {
    val categoryChooserFragment = container.getFragment<CategoryChooserFragment>()

    categoryChooserFragment.setClickHandler(handler)
}

@BindingAdapter("categoryChooserGridThumbnailSize")
fun bindCategoryChooserGridThumbnailSize(container: FragmentContainerView, size: GridThumbnailSize) {
    val categoryChooserFragment = container.getFragment<CategoryChooserFragment>()

    categoryChooserFragment.setGridThumbnailSize(size)
}

@BindingAdapter("categoryChooserShowYearInList")
fun bindCategoryChooserShowYearInList(container: FragmentContainerView, show: Boolean) {
    val categoryChooserFragment = container.getFragment<CategoryChooserFragment>()

    categoryChooserFragment.setShowYearsInList(show)
}