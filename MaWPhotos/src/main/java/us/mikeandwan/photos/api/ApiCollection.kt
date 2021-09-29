package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class ApiCollection<T> {
    @JsonProperty("count") var count: Long = 0
    @JsonProperty("items") var items: List<T> = ArrayList()
}