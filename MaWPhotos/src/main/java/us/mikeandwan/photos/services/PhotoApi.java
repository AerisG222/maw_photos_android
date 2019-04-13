package us.mikeandwan.photos.services;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import us.mikeandwan.photos.models.ApiCollection;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.models.CommentPhoto;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.models.FileOperationResult;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.RatePhoto;
import us.mikeandwan.photos.models.Rating;


interface PhotoApi {
    @GET("photo-categories/recent/{sinceId}")
    Call<ApiCollection<Category>> getRecentCategories(@Path("sinceId") int sinceId);

    @GET("photos/{photoId}/exif")
    Call<ExifData> getExifData(@Path("photoId") int photoId);

    @GET("photos/random")
    Call<Photo> getRandomPhoto();

    @GET("photos/random/{count}")
    Call<ApiCollection<Photo>> getRandomPhotos(@Path("count") int count);

    @GET("photos/{photoId}/comments")
    Call<ApiCollection<Comment>> getComments(@Path("photoId") int photoId);

    @GET("photos/{photoId}/rating")
    Call<Rating> getRatings(@Path("photoId") int photoId);

    @GET("photo-categories/{categoryId}/photos")
    Call<ApiCollection<Photo>> getPhotosByCategory(@Path("categoryId") int categoryId);

    @PATCH("photos/{photoId}/rating")
    Call<Rating> ratePhoto(@Path("photoId") int photoId, @Body RatePhoto rating);

    @POST("photos/{photoId}/comments")
    Call<ApiCollection<Comment>> addCommentForPhoto(@Path("photoId") int photoId, @Body CommentPhoto commentPhoto);

    @Multipart
    @POST("upload/upload")
    Call<FileOperationResult> uploadFile(@Part MultipartBody.Part file);
}
