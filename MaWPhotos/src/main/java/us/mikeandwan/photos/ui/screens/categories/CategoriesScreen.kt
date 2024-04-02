package us.mikeandwan.photos.ui.screens.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryList
import us.mikeandwan.photos.ui.controls.categorylist.CategoryListViewModel
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridScreen
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridViewModel
import us.mikeandwan.photos.ui.toImageGridItem

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel
) {
    val prefs = viewModel.preferences.collectAsState()
    val categories = viewModel.categories.collectAsState()

    when(prefs.value.displayType) {
        CategoryDisplayType.Grid -> {
            val vm = ImageGridViewModel()
            vm.setGridItems(categories.value.map{ it.toImageGridItem() })
            vm.setThumbnailSize(prefs.value.gridThumbnailSize)

            ImageGridScreen(
                viewModel = vm,
                onSelectGridItem = {
                    viewModel.onCategorySelected(it.data as PhotoCategory)
                }
            )
        }
        CategoryDisplayType.List -> {
            val vm = CategoryListViewModel()
            vm.setCategories(categories.value)

            CategoryList(
                viewModel = vm,
                onSelectListItem = viewModel.onCategorySelected
            )
        }
        else -> { }
    }
}

