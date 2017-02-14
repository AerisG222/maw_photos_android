package us.mikeandwan.photos.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.models.CommentPhoto;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.models.RatePhoto;
import us.mikeandwan.photos.models.Rating;


interface PhotoApi {
    @FormUrlEncoded
    @POST("api/account/login")
    Call<Boolean> authenticate(@Field("Username") String username, @Field("Password") String password);

    @GET("api/photos/getPhotoYears")
    Call<List<Integer>> getPhotoYears();

    @GET("api/photos/getCategoriesForYear/{year}")
    Call<List<Category>> getCategoriesForYear(@Path("year") int year);

    @GET("api/account/get-xsrf-token")
    Call<Boolean> establishXsrfTokenCookie();

    @GET("api/photos/getRecentCategories/{sinceId}")
    Call<List<Category>> getRecentCategories(@Path("sinceId") int sinceId);

    @GET("api/photos/getCategoryCount")
    Call<Integer> getTotalCategoryCount();

    @GET("api/photos/getPhotoExifData/{photoId}")
    Call<ExifData> getExifData(@Path("photoId") int photoId);

    @GET("api/photos/getRandomPhoto")
    Call<PhotoAndCategory> getRandomPhoto();

    @GET("api/photos/getCommentsForPhoto/{photoId}")
    Call<List<Comment>> getComments(@Path("photoId") int photoId);

    @GET("api/photos/getRatingForPhoto/{photoId}")
    Call<Rating> getRatings(@Path("photoId") int photoId);

    @GET("api/photos/getPhotosByCategory/{categoryId}")
    Call<List<Photo>> getPhotosByCategory(@Path("categoryId") int categoryId);

    @GET("api/photos/getPhotosByCommentDate/{newestFirst}")
    Call<List<Photo>> getPhotosByCommentDate(@Path("newestFirst") boolean newestFirst);

    @GET("api/photos/getPhotosByUserCommentDate/{newestFirst}")
    Call<List<Photo>> getPhotosByUserCommentDate(@Path("newestFirst") boolean newestFirst);

    @GET("api/photos/getPhotosByCommentCount/{mostFirst}")
    Call<List<Photo>> getPhotosByCommentCount(@Path("most") boolean mostFirst);

    @GET("api/photos/getPhotosByAverageRating/true")
    Call<List<Photo>> getPhotosByAverageRating();

    @GET("api/photos/getPhotosByUserRating/true")
    Call<List<Photo>> getPhotosByUserRating();

    @POST("api/photos/ratePhoto")
    Call<Float> ratePhoto(@Body RatePhoto rating);

    @POST("api/photos/addCommentForPhoto")
    Call<List<Comment>> addCommentForPhoto(@Body CommentPhoto commentPhoto);
}
