package us.mikeandwan.photos.services;

import android.util.Log;
import android.webkit.MimeTypeMap;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.ApiCollection;
import us.mikeandwan.photos.models.ApiResult;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.models.CommentPhoto;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.models.FileOperationResult;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.RatePhoto;
import us.mikeandwan.photos.models.Rating;


public class PhotoApiClient {
    private final PhotoApi _photoApi;
    private final OkHttpClient _httpClient;
    private final MimeTypeMap _map = MimeTypeMap.getSingleton();


    @Inject
    public PhotoApiClient(OkHttpClient httpClient,
                          Retrofit retrofit) {
        _httpClient = httpClient;
        _photoApi = retrofit.create(PhotoApi.class);
    }


    ApiCollection<Category> getRecentCategories(int sinceId) throws IOException {
        Log.d(MawApplication.LOG_TAG, "getRecentCategories starting");

        Response<ApiCollection<Category>> response = _photoApi.getRecentCategories(sinceId).execute();
        ApiResult<ApiCollection<Category>> result = new ApiResult<>(response);

        if(!result.isSuccess()) {
            Log.w(MawApplication.LOG_TAG, String.format("getRecentCategories failed: %s", result.getError()));
            return null;
        }

        Log.d(MawApplication.LOG_TAG, String.format("getRecentCategories succeeded: %d categories found", result.getResult().getCount()));

        return result.getResult();
    }


    ApiCollection<Photo> getPhotos(PhotoListType type, int categoryId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "getPhotos starting");

        Response<ApiCollection<Photo>> response = _photoApi.getPhotosByCategory(categoryId).execute();
        ApiResult<ApiCollection<Photo>> result = new ApiResult<>(response);

        if(!result.isSuccess()) {
            Log.w(MawApplication.LOG_TAG, String.format("getPhotos failed: %s", result.getError()));
            return null;
        }

        Log.d(MawApplication.LOG_TAG, String.format("getRecentCategories succeeded: %d categories found", result.getResult().getCount()));

        return result.getResult();
    }


    Photo getRandomPhoto() throws IOException {
        Log.d(MawApplication.LOG_TAG, "getRandomPhoto starting");

        Response<Photo> response = _photoApi.getRandomPhoto().execute();
        ApiResult<Photo> result = new ApiResult<>(response);

        if(!result.isSuccess()) {
            Log.w(MawApplication.LOG_TAG, String.format("getRandomPhoto failed: %s", result.getError()));
            return null;
        }

        Log.d(MawApplication.LOG_TAG, "getRandomPhoto succeeded");

        return result.getResult();
    }


    ApiCollection<Photo> getRandomPhotos(int count) throws IOException {
        Log.d(MawApplication.LOG_TAG, "getRandomPhotos starting");

        Response<ApiCollection<Photo>> response = _photoApi.getRandomPhotos(count).execute();
        ApiResult<ApiCollection<Photo>> result = new ApiResult<>(response);

        if(!result.isSuccess()) {
            Log.w(MawApplication.LOG_TAG, String.format("getRandomPhotos failed: %s", result.getError()));
            return null;
        }

        Log.d(MawApplication.LOG_TAG, "getRandomPhotos succeeded");

        return result.getResult();
    }

    ExifData getExifData(int photoId) throws IOException {
        Log.d(MawApplication.LOG_TAG, "getExifData starting");

        Response<ExifData> response = _photoApi.getExifData(photoId).execute();
        ApiResult<ExifData> result = new ApiResult<>(response);

        if(!result.isSuccess()) {
            Log.w(MawApplication.LOG_TAG, String.format("getExifData failed: %s", result.getError()));
            return null;
        }

        Log.d(MawApplication.LOG_TAG, "getExifData succeeded");

        return result.getResult();
    }


    ApiCollection<Comment> getComments(int photoId) throws IOException {
        Log.d(MawApplication.LOG_TAG, "getComments starting");

        Response<ApiCollection<Comment>> response = _photoApi.getComments(photoId).execute();
        ApiResult<ApiCollection<Comment>> result = new ApiResult<>(response);

        if(!result.isSuccess()) {
            Log.w(MawApplication.LOG_TAG, String.format("getComments failed: %s", result.getError()));
            return null;
        }

        Log.d(MawApplication.LOG_TAG, String.format("getComments succeeded, %d comments found.", result.getResult().getCount()));

        return result.getResult();
    }


    Rating getRatings(int photoId) throws IOException {
        Log.d(MawApplication.LOG_TAG, "getRatings starting");

        Response<Rating> response = _photoApi.getRatings(photoId).execute();
        ApiResult<Rating> result = new ApiResult<>(response);

        if(!result.isSuccess()) {
            Log.w(MawApplication.LOG_TAG, String.format("getRatings failed: %s", result.getError()));
            return null;
        }

        Log.d(MawApplication.LOG_TAG, "getRatings succeeded");

        return result.getResult();
    }


    Float setRating(int photoId, int rating) throws IOException {
        RatePhoto rp = new RatePhoto();
        rp.setPhotoId(photoId);
        rp.setRating(rating);

        Log.d(MawApplication.LOG_TAG, "setRating starting");

        Response<Rating> response = _photoApi.ratePhoto(photoId, rp).execute();
        ApiResult<Rating> result = new ApiResult<>(response);

        if(!result.isSuccess()) {
            Log.w(MawApplication.LOG_TAG, String.format("setRating failed: %s", result.getError()));
            return null;
        }

        Log.d(MawApplication.LOG_TAG, "setRating succeeded");

        return result.getResult().getAverageRating();
    }


    void addComment(int photoId, String comment) throws IOException {
        CommentPhoto cp = new CommentPhoto();
        cp.setComment(comment);
        cp.setPhotoId(photoId);

        Log.d(MawApplication.LOG_TAG, "addComment starting");

        Response<ApiCollection<Comment>> response = _photoApi.addCommentForPhoto(photoId, cp).execute();
        ApiResult<ApiCollection<Comment>> result = new ApiResult<>(response);

        if(!result.isSuccess()) {
            Log.w(MawApplication.LOG_TAG, String.format("addComment failed: %s", result.getError()));
        }

        Log.d(MawApplication.LOG_TAG, "addComment succeeded");
    }


    okhttp3.Response downloadPhoto(String photoUrl) {
        try {
            URL url = new URL(photoUrl);
            Request request = new Request.Builder().url(url).build();

            return _httpClient.newCall(request).execute();
        } catch (IOException ex) {
            Log.w(MawApplication.LOG_TAG, "Error when getting photo blob: " + ex.getMessage());
        }

        return null;
    }


    // https://futurestud.io/tutorials/retrofit-2-how-to-upload-files-to-server
    FileOperationResult uploadFile(File file) throws IOException {
        try {
            MediaType type = MediaType.parse(_map.getMimeTypeFromExtension(FilenameUtils.getExtension(file.getName())));
            RequestBody requestFile = RequestBody.create(type, file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            Response<FileOperationResult> response = _photoApi.uploadFile(body).execute();

            if(response.isSuccessful()) {
                Log.w(MawApplication.LOG_TAG, "upload succeeded for file: " + file.getName());
                return response.body();
            } else {
                Log.w(MawApplication.LOG_TAG, "unable to upload file: " + file.getName());
            }
        } catch (IOException ex) {
            Log.w(MawApplication.LOG_TAG, "Error uploading file: " + file.getName() + ": " + ex.getMessage());
            throw ex;
        }

        return null;
    }
}
