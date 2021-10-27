package us.mikeandwan.photos.uiold.categories

import us.mikeandwan.photos.models.Category

interface ICategoryListActivity {
    fun selectCategory(category: Category?)
    fun onApiException(throwable: Throwable?)
}