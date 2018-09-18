package us.mikeandwan.photos.services;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.models.CommentPhoto;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.models.FileOperationResult;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.models.RatePhoto;
import us.mikeandwan.photos.models.Rating;


interface PhotoApi {
    /*
    @GET("photos/getPhotoYears")
    Call<List<Integer>> getPhotoYears();

    @GET("photos/getCategoriesForYear/{year}")
    Call<List<Category>> getCategoriesForYear(@Path("year") int year);
    */

    @GET("photos/getRecentCategories/{sinceId}")
    Call<List<Category>> getRecentCategories(@Path("sinceId") int sinceId);

    /*
    @GET("photos/getCategoryCount")
    Call<Integer> getTotalCategoryCount();
    */

    @GET("photos/getPhotoExifData/{photoId}")
    Call<ExifData> getExifData(@Path("photoId") int photoId);

    @GET("photos/getRandomPhoto")
    Call<PhotoAndCategory> getRandomPhoto();

    @GET("photos/getCommentsForPhoto/{photoId}")
    Call<List<Comment>> getComments(@Path("photoId") int photoId);

    @GET("photos/getRatingForPhoto/{photoId}")
    Call<Rating> getRatings(@Path("photoId") int photoId);

    @GET("photos/getPhotosByCategory/{categoryId}")
    Call<List<Photo>> getPhotosByCategory(@Path("categoryId") int categoryId);

    @GET("photos/getPhotosByCommentDate/{newestFirst}")
    Call<List<Photo>> getPhotosByCommentDate(@Path("newestFirst") boolean newestFirst);

    @GET("photos/getPhotosByUserCommentDate/{newestFirst}")
    Call<List<Photo>> getPhotosByUserCommentDate(@Path("newestFirst") boolean newestFirst);

    @GET("photos/getPhotosByCommentCount/{mostFirst}")
    Call<List<Photo>> getPhotosByCommentCount(@Path("mostFirst") boolean mostFirst);

    @GET("photos/getPhotosByAverageRating/true")
    Call<List<Photo>> getPhotosByAverageRating();

    @GET("photos/getPhotosByUserRating/true")
    Call<List<Photo>> getPhotosByUserRating();

    @POST("photos/ratePhoto")
    Call<Float> ratePhoto(@Body RatePhoto rating);

    @POST("photos/addCommentForPhoto")
    Call<Boolean> addCommentForPhoto(@Body CommentPhoto commentPhoto);

    @Multipart
    @POST("upload/upload")
    Call<FileOperationResult> uploadFile(@Part MultipartBody.Part file);
}
