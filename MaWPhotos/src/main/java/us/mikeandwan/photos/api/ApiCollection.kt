package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiCollection<T>(
    val count: Long = 0,
    val items: List<T> = ArrayList()
)