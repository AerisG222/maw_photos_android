package us.mikeandwan.photos.api

import retrofit2.Response
import retrofit2.http.*

internal interface VideoApi {
    @GET("video-categories/recent/{sinceId}")
    suspend fun getRecentCategories(@Path("sinceId") sinceId: Int): Response<ApiCollection<Category>>

    @GET("videos/{videoId}/comments")
    suspend fun getComments(@Path("videoId") videoId: Int): Response<ApiCollection<Comment>>

    @GET("videos/{videoId}/rating")
    suspend fun getRatings(@Path("videoId") videoId: Int): Response<Rating>

    @GET("video-categories/{categoryId}/videos")
    suspend fun getVideosByCategory(@Path("categoryId") categoryId: Int): Response<ApiCollection<Video>>

    @PATCH("videos/{videoId}/rating")
    suspend fun rateVideo(@Path("videoId") videoId: Int, @Body rating: RateVideo): Response<Rating>

    @POST("videos/{videoId}/comments")
    suspend fun addCommentForVideo(
        @Path("videoId") videoId: Int,
        @Body commentPhoto: CommentVideo
    ): Response<ApiCollection<Comment>>
}
