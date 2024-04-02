package us.mikeandwan.photos.ui

import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentContainerView
import us.mikeandwan.photos.domain.models.*
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
