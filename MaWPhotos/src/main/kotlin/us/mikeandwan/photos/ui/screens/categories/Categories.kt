package us.mikeandwan.photos.ui.screens.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import us.mikeandwan.photos.domain.models.CategoryDisplayType
import us.mikeandwan.photos.domain.models.CategoryPreference
import us.mikeandwan.photos.domain.models.PhotoCategory
import us.mikeandwan.photos.ui.controls.categorylist.CategoryList
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridScreen
import us.mikeandwan.photos.ui.controls.imagegrid.ImageGridViewModel
import us.mikeandwan.photos.ui.toImageGridItem

const val CategoriesRoute = "categories"

fun NavGraphBuilder.categoriesScreen(
    onNavigateToCategory: (PhotoCategory) -> Unit
) {
    composable(CategoriesRoute) {
        val vm: CategoriesViewModel = hiltViewModel()

        val categories by vm.categories.collectAsStateWithLifecycle()
        val preferences by vm.preferences.collectAsStateWithLifecycle()

        CategoriesScreen(
            categories = categories,
            preferences = preferences,
            onNavigateToCategory = onNavigateToCategory
        )
    }
}

fun NavController.navigateToCategories() {
    this.navigate(CategoriesRoute)
}

@Composable
fun CategoriesScreen(
    preferences: CategoryPreference,
    categories: List<PhotoCategory>,
    onNavigateToCategory: (PhotoCategory) -> Unit
) {
    when(preferences.displayType) {
        CategoryDisplayType.Grid -> {
            val vm = ImageGridViewModel()
            vm.setGridItems(categories.map{ it.toImageGridItem() })
            vm.setThumbnailSize(preferences.gridThumbnailSize)

            ImageGridScreen(
                viewModel = vm,
                onSelectGridItem = {
                    onNavigateToCategory(it.data as PhotoCategory)
                }
            )
        }
        CategoryDisplayType.List -> {
            CategoryList(
                categories = categories,
                showYear = false,
                onSelectCategory = { onNavigateToCategory(it) }
            )
        }
        else -> { }
    }
}


//                viewModel.refreshStatus
//                    .onEach {
//                        if(it.message != null) {
//                            val snackbar = Snackbar.make(binding.container.context, binding.root, it.message, Snackbar.LENGTH_SHORT)
//
//                            snackbar.show()
//                        }
//                    }
//                    .launchIn(this)