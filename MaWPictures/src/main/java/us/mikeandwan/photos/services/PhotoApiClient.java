package us.mikeandwan.photos.services;

import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import us.mikeandwan.photos.Constants;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.models.CommentPhoto;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.models.RatePhoto;
import us.mikeandwan.photos.models.Rating;


public class PhotoApiClient {
    private final PhotoApiCookieJar _cookieJar;
    private final PhotoApi _photoApi;
    private final OkHttpClient _httpClient;


    @Inject
    public PhotoApiClient(OkHttpClient httpClient,
                          Retrofit retrofit,
                          PhotoApiCookieJar cookieJar) {
        _httpClient = httpClient;
        _photoApi = retrofit.create(PhotoApi.class);
        _cookieJar = cookieJar;
    }


    public boolean isAuthenticated() {
        return _cookieJar.isAuthenticated();
    }


    public boolean authenticate(String username, String password) {
        try {
            Response<Boolean> result = _photoApi.authenticate(username, password).execute();

            if(result.isSuccessful()) {
                establishXsrfTokenCookie();
                return true;
            }
        } catch (Exception ex) {
            Log.w(MawApplication.LOG_TAG, "Error when authenticating: " + ex.getMessage());
        }

        return false;
    }


    public List<Category> getRecentCategories(int sinceId) throws IOException {
        Response<List<Category>> response = _photoApi.getRecentCategories(sinceId).execute();

        return response.body();
    }


    public List<Photo> getPhotos(PhotoListType type, int categoryId) throws Exception {
        Call<List<Photo>> call;

        switch(type) {
            case ByCategory:
                call = _photoApi.getPhotosByCategory(categoryId);
                break;
            case ByCommentsNewest:
                call = _photoApi.getPhotosByCommentDate(true);
                break;
            case ByCommentsOldest:
                call = _photoApi.getPhotosByCommentDate(false);
                break;
            case ByUserCommentsNewest:
                call = _photoApi.getPhotosByUserCommentDate(true);
                break;
            case ByUserCommentsOldest:
                call = _photoApi.getPhotosByUserCommentDate(false);
                break;
            case ByCommentCountMost:
                call = _photoApi.getPhotosByCommentCount(true);
                break;
            case ByCommentCountLeast:
                call = _photoApi.getPhotosByCommentCount(false);
                break;
            case ByAverageRating:
                call = _photoApi.getPhotosByAverageRating();
                break;
            case ByUserRating:
                call = _photoApi.getPhotosByUserRating();
                break;
            default:
                throw new Exception("Unknown photo list type!");
        }

        return call.execute().body();
    }


    public PhotoAndCategory getRandomPhoto() throws IOException {
        Response<PhotoAndCategory> response = _photoApi.getRandomPhoto().execute();

        return response.body();
    }


    public ExifData getExifData(int photoId) throws IOException {
        Response<ExifData> response = _photoApi.getExifData(photoId).execute();

        return response.body();
    }


    public List<Comment> getComments(int photoId) throws IOException {
        Response<List<Comment>> response = _photoApi.getComments(photoId).execute();

        return response.body();
    }


    public Rating getRatings(int photoId) throws IOException {
        Response<Rating> response = _photoApi.getRatings(photoId).execute();

        return response.body();
    }


    public Float setRating(int photoId, int rating) {
        RatePhoto rp = new RatePhoto();
        rp.setPhotoId(photoId);
        rp.setRating(rating);

        try {
            Response<Float> response = _photoApi.ratePhoto(rp).execute();

            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.w(MawApplication.LOG_TAG, "unable to save rating!");
            }
        } catch (MalformedURLException ex) {
            Log.e(MawApplication.LOG_TAG, "invalid url!");
        } catch (Exception ex) {
            Log.e(MawApplication.LOG_TAG, "error trying to save rating: " + ex.getMessage());
        }

        return null;
    }


    public void addComment(int photoId, String comment) {
        CommentPhoto cp = new CommentPhoto();
        cp.setComment(comment);
        cp.setPhotoId(photoId);

        try {
            Response<Boolean> response = _photoApi.addCommentForPhoto(cp).execute();

            if(response.isSuccessful()) {
                Log.w(MawApplication.LOG_TAG, "got response: " + response.code());
            } else {
                Log.w(MawApplication.LOG_TAG, "unable to save comment!");
            }
        }
        catch(Exception ex) {
            Log.e(MawApplication.LOG_TAG, "Error trying to add comment: " + ex.getMessage());
        }
    }


    public okhttp3.Response downloadPhoto(String photoPath) {
        try {
            URL url = new URL(buildPhotoUrl(photoPath));
            Request request = new Request.Builder().url(url).build();

            return _httpClient.newCall(request).execute();
        } catch (IOException ex) {
            Log.w(MawApplication.LOG_TAG, "Error when getting photo blob: " + ex.getMessage());
        }

        return null;
    }


    private static String buildPhotoUrl(String photoPath) {
        if (photoPath.startsWith("/")) {
            return Constants.SITE_URL + photoPath.substring(1);
        }

        return Constants.SITE_URL + photoPath;
    }


    private void establishXsrfTokenCookie() {
        try {
            Response<Boolean> response = _photoApi.establishXsrfTokenCookie().execute();

            if (response.isSuccessful()) {
                Log.i(MawApplication.LOG_TAG, "Obtained XSRF token");
            }
        } catch (Exception ex) {
            Log.w(MawApplication.LOG_TAG, "Error obtaining XSRF token: " + ex.getMessage());
        }
    }
}
