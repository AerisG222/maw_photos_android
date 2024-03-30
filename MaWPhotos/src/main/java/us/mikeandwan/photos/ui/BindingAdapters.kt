package us.mikeandwan.photos.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentContainerView
import io.noties.markwon.Markwon
import us.mikeandwan.photos.domain.models.*
import us.mikeandwan.photos.ui.controls.categorychooser.CategoryChooserFragment
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridFragment
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridItem
import java.io.File

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

@BindingAdapter("categoryChooserRefreshCategoriesHandler")
fun bindCategoryChooserRefreshHandler(container: FragmentContainerView, handler: CategoryChooserFragment.RefreshCategoriesListener) {
    val categoryChooserFragment = container.getFragment<CategoryChooserFragment>()

    categoryChooserFragment.setRefreshHandler(handler)
}

@BindingAdapter("categoryChooserRefreshStatus")
fun bindCategoryChooserRefreshStatus(container: FragmentContainerView, status: CategoryRefreshStatus) {
    val categoryChooserFragment = container.getFragment<CategoryChooserFragment>()

    categoryChooserFragment.setRefreshStatus(status)
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

@BindingAdapter("categoryChooserEnableRefresh")
fun bindCategoryChooserEnableRefresh(container: FragmentContainerView, enable: Boolean) {
    val categoryChooserFragment = container.getFragment<CategoryChooserFragment>()

    categoryChooserFragment.setEnableRefresh(enable)
}
