package us.mikeandwan.photos.api

import retrofit2.Retrofit
import javax.inject.Inject

class VideoApiClient @Inject constructor(
    retrofit: Retrofit
): BaseApiClient() {
    private val _videoApi: VideoApi by lazy { retrofit.create(VideoApi::class.java) }

    suspend fun getRecentCategories(sinceId: Int): ApiResult<ApiCollection<Category>> {
        return makeApiCall(::getRecentCategories.name, suspend { _videoApi.getRecentCategories(sinceId) })
    }

    suspend fun getVideos(categoryId: Int): ApiResult<ApiCollection<Video>> {
        return makeApiCall(::getVideos.name, suspend { _videoApi.getVideosByCategory(categoryId) })
    }

    suspend fun getComments(videoId: Int): ApiResult<ApiCollection<Comment>> {
        return makeApiCall(::getComments.name, suspend { _videoApi.getComments(videoId) })
    }

    suspend fun getRatings(videoId: Int): ApiResult<Rating> {
        return makeApiCall(::getRatings.name, suspend { _videoApi.getRatings(videoId) })
    }

    suspend fun setRating(videoId: Int, rating: Short): ApiResult<Rating> {
        val rv = RateVideo(videoId, rating)

        return makeApiCall(::setRating.name, suspend { _videoApi.rateVideo(videoId, rv) })
    }

    suspend fun addComment(videoId: Int, comment: String): ApiResult<ApiCollection<Comment>> {
        val cv = CommentVideo(videoId, comment)

        return makeApiCall(::addComment.name, suspend { _videoApi.addCommentForVideo(videoId, cv) })
    }
}
