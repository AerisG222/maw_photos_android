package us.mikeandwan.photos.services

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import us.mikeandwan.photos.models.*

internal interface PhotoApi {
    @GET("photo-categories/recent/{sinceId}")
    fun getRecentCategories(@Path("sinceId") sinceId: Int): Call<ApiCollection<Category>>

    @GET("photos/{photoId}/exif")
    fun getExifData(@Path("photoId") photoId: Int): Call<ExifData>

    @GET("photos/random")
    fun getRandomPhoto(): Call<Photo>

    @GET("photos/random/{count}")
    fun getRandomPhotos(@Path("count") count: Int): Call<ApiCollection<Photo>>

    @GET("photos/{photoId}/comments")
    fun getComments(@Path("photoId") photoId: Int): Call<ApiCollection<Comment>>

    @GET("photos/{photoId}/rating")
    fun getRatings(@Path("photoId") photoId: Int): Call<Rating>

    @GET("photo-categories/{categoryId}/photos")
    fun getPhotosByCategory(@Path("categoryId") categoryId: Int): Call<ApiCollection<Photo>>

    @PATCH("photos/{photoId}/rating")
    fun ratePhoto(@Path("photoId") photoId: Int, @Body rating: RatePhoto): Call<Rating>

    @POST("photos/{photoId}/comments")
    fun addCommentForPhoto(
        @Path("photoId") photoId: Int,
        @Body commentPhoto: CommentPhoto
    ): Call<ApiCollection<Comment>>

    @Multipart
    @POST("upload/upload")
    fun uploadFile(@Part file: MultipartBody.Part): Call<FileOperationResult>
}