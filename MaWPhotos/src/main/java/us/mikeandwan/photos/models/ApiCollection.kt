package us.mikeandwan.photos.models

import java.util.*

class ApiCollection<T> {
    var count: Long = 0
    var items: List<T> = ArrayList()
}