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


    public ApiCollection<Category> getRecentCategories(int sinceId) throws IOException {
        Response<ApiCollection<Category>> response = _photoApi.getRecentCategories(sinceId).execute();

        if(!response.isSuccessful()) {
            ResponseBody body = response.errorBody();

            if(body != null) {
                Log.e(MawApplication.LOG_TAG, String.format("getRecentCategories response: %d | %s", response.code(), body.string()));
            } else {
                Log.e(MawApplication.LOG_TAG, String.format("getRecentCategories response: %d | %s", response.code(), response.message()));
            }
        }

        return response.body();
    }


    public ApiCollection<Photo> getPhotos(PhotoListType type, int categoryId) throws Exception {
        Response<ApiCollection<Photo>> response = _photoApi.getPhotosByCategory(categoryId).execute();

        return response.body();
    }


    public Photo getRandomPhoto() throws IOException {
        Response<Photo> response = _photoApi.getRandomPhoto().execute();

        return response.body();
    }


    public ApiCollection<Photo> getRandomPhotos(int count) throws IOException {
        Response<ApiCollection<Photo>> response = _photoApi.getRandomPhotos(count).execute();

        return response.body();
    }

    public ExifData getExifData(int photoId) throws IOException {
        Response<ExifData> response = _photoApi.getExifData(photoId).execute();

        return response.body();
    }


    public ApiCollection<Comment> getComments(int photoId) throws IOException {
        Response<ApiCollection<Comment>> response = _photoApi.getComments(photoId).execute();

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
            Response<Rating> response = _photoApi.ratePhoto(photoId, rp).execute();

            if (response.isSuccessful()) {
                return response.body().getAverageRating();
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
            Response<ApiCollection<Comment>> response = _photoApi.addCommentForPhoto(photoId, cp).execute();

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


    public okhttp3.Response downloadPhoto(String photoUrl) {
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
    public FileOperationResult uploadFile(File file) throws IOException {
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
