package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class FileLocation (
    @JsonProperty("username") val username: String,
    @JsonProperty("filename") val filename: String,
    @JsonProperty("relativePath") val relativePath: String,
)
