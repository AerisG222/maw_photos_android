package us.mikeandwan.photos.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
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
import us.mikeandwan.photos.models.Credentials;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.models.RatePhoto;
import us.mikeandwan.photos.models.Rating;


public class PhotoApiClient {
    private final Context _context;
    private final PhotoStorage _photoStorage;
    private final MawDataManager _dataManager;
    private final PhotoApiCookieJar _cookieJar;
    private final PhotoApi _photoApi;
    private final OkHttpClient _httpClient;


    @Inject
    public PhotoApiClient(Context context,
                          PhotoStorage photoStorage,
                          MawDataManager dataManager,
                          OkHttpClient httpClient,
                          Retrofit retrofit,
                          PhotoApiCookieJar cookieJar) {
        _context = context;
        _photoStorage = photoStorage;
        _dataManager = dataManager;
        _httpClient = httpClient;
        _photoApi = retrofit.create(PhotoApi.class);
        _cookieJar = cookieJar;
    }


    public boolean isConnected() {
        ConnectivityManager mgr = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = mgr.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();
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


    public List<Integer> getPhotoYears() throws MawAuthenticationException, IOException {
        ensureAuthenticated(false);

        Response<List<Integer>> response = _photoApi.getPhotoYears().execute();

        if (response.isSuccessful()) {
            return response.body();
        }

        Log.e(MawApplication.LOG_TAG, "error getting photo years: " + response.toString());

        throw new MawAuthenticationException();
    }


    public List<Category> getCategoriesForYear(int year) throws MawAuthenticationException, IOException {
        ensureAuthenticated(false);

        Response<List<Category>> response = _photoApi.getCategoriesForYear(year).execute();

        return response.body();
    }


    public List<Category> getRecentCategories(int sinceId) throws MawAuthenticationException, IOException {
        ensureAuthenticated(false);

        Response<List<Category>> response = _photoApi.getRecentCategories(sinceId).execute();

        return response.body();
    }


    public int getTotalCategoryCount() throws MawAuthenticationException, IOException {
        ensureAuthenticated(false);

        Response<Integer> response = _photoApi.getTotalCategoryCount().execute();

        return response.body();
    }


    public List<Photo> getPhotos(PhotoListType type, int categoryId) throws Exception {
        ensureAuthenticated(false);

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


    public PhotoAndCategory getRandomPhoto() throws MawAuthenticationException, IOException {
        ensureAuthenticated(false);

        Response<PhotoAndCategory> response = _photoApi.getRandomPhoto().execute();

        return response.body();
    }


    public ExifData getExifData(int photoId) throws MawAuthenticationException, IOException {
        ensureAuthenticated(false);

        Response<ExifData> response = _photoApi.getExifData(photoId).execute();

        return response.body();
    }


    public List<Comment> getComments(int photoId) throws MawAuthenticationException, IOException {
        ensureAuthenticated(false);

        Response<List<Comment>> response = _photoApi.getComments(photoId).execute();

        return response.body();
    }


    public Rating getRatings(int photoId) throws MawAuthenticationException, IOException {
        ensureAuthenticated(false);

        Response<Rating> response = _photoApi.getRatings(photoId).execute();

        return response.body();
    }


    public Float setRating(int photoId, int rating) throws MawAuthenticationException {
        ensureAuthenticated(false);

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


    public void addComment(int photoId, String comment) throws MawAuthenticationException {
        ensureAuthenticated(false);

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


    public boolean downloadPhoto(String photoPath) throws MawAuthenticationException {
        ensureAuthenticated(false);

        if (photoPath == null || TextUtils.isEmpty(photoPath)) {
            Log.w(MawApplication.LOG_TAG, "you need to specify the path to the photo");
        }

        if (_photoStorage.doesExist(photoPath)) {
            return true;
        }

        try {
            URL url = new URL(buildPhotoUrl(photoPath));
            Request request = new Request.Builder().url(url).build();
            okhttp3.Response response = _httpClient.newCall(request).execute();

            if(response.isSuccessful()) {
                _photoStorage.put(photoPath, response.body());

                return true;
            }
            else {
                Log.w(MawApplication.LOG_TAG, "Did not receive a successful download status code: " + String.valueOf(response.code()));
            }
        } catch (IOException ex) {
            Log.w(MawApplication.LOG_TAG, "Error when getting photo blob: " + ex.getMessage());
        }

        return false;
    }


    // this should only be called by methods that already expect to make network calls, otherwise android will complain
    // about this running on the ui thread
    void ensureAuthenticated(boolean force) throws MawAuthenticationException {
        if (force || !isAuthenticated()) {
            Log.d(MawApplication.LOG_TAG, "refreshing the authentication token");

            Credentials creds = _dataManager.getCredentials();

            if (creds != null) {
                String username = creds.getUsername();
                String password = creds.getPassword();

                if (!authenticate(username, password)) {
                    Log.e(MawApplication.LOG_TAG, "unable to reinstate the authentication token");
                    throw new MawAuthenticationException();
                }
            }
        }
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
