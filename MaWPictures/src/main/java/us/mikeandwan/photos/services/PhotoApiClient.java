package us.mikeandwan.photos.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.mikeandwan.photos.Constants;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Category;
import us.mikeandwan.photos.data.Comment;
import us.mikeandwan.photos.data.CommentPhoto;
import us.mikeandwan.photos.data.Credentials;
import us.mikeandwan.photos.data.ExifData;
import us.mikeandwan.photos.data.MawDataManager;
import us.mikeandwan.photos.data.Photo;
import us.mikeandwan.photos.data.PhotoAndCategory;
import us.mikeandwan.photos.data.RatePhoto;
import us.mikeandwan.photos.data.Rating;


public class PhotoApiClient {
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final String AUTH_COOKIE_NAME = "maw_auth";
    private static final String API_LOGIN_URL = Constants.SITE_URL + "api/account/login";
    private static final String API_GET_PHOTO_YEARS_URL = Constants.SITE_URL + "api/photos/getPhotoYears";
    private static final String API_GET_RECENT_CATEGORIES_URL = Constants.SITE_URL + "api/photos/getRecentCategories";
    private static final String API_GET_CATEGORY_COUNT_URL = Constants.SITE_URL + "api/photos/getCategoryCount";
    private static final String API_CATEGORIES_FOR_YEAR_URL = Constants.SITE_URL + "api/photos/getCategoriesForYear";
    private static final String API_PHOTOS_BY_CATEGORY_URL = Constants.SITE_URL + "api/photos/getPhotosByCategory";
    private static final String API_PHOTOS_BY_COMMENT_DATE_URL = Constants.SITE_URL + "api/photos/getPhotosByCommentDate";
    private static final String API_PHOTOS_BY_USER_COMMENT_DATE_URL = Constants.SITE_URL + "api/photos/getPhotosByUserCommentDate";
    private static final String API_PHOTOS_BY_COMMENT_COUNT_URL = Constants.SITE_URL + "api/photos/getPhotosByCommentCount";
    private static final String API_PHOTOS_BY_AVERAGE_RATING_URL = Constants.SITE_URL + "api/photos/getPhotosByAverageRating";
    private static final String API_PHOTOS_BY_USER_RATING_URL = Constants.SITE_URL + "api/photos/getPhotosByUserRating";
    public static final String API_GET_PHOTO_RATING_URL = Constants.SITE_URL + "api/photos/getRatingForPhoto";
    public static final String API_SET_PHOTO_RATING_URL = Constants.SITE_URL + "api/photos/ratePhoto";
    public static final String API_GET_PHOTO_COMMENTS_URL = Constants.SITE_URL + "api/photos/getCommentsForPhoto";
    public static final String API_ADD_PHOTO_COMMENT_URL = Constants.SITE_URL + "api/photos/addCommentForPhoto";
    public static final String API_GET_PHOTO_EXIF_URL = Constants.SITE_URL + "api/photos/getPhotoExifData";
    public static final String API_GET_RANDOM_PHOTO_URL = Constants.SITE_URL + "api/photos/getRandomPhoto";

    private static CookieManager _cookieManager = new CookieManager();
    private static PhotoStorage _photoStorage;

    private boolean _isSecondAttempt;
    private Context _context;


    static {
        CookieHandler.setDefault(_cookieManager);
    }


    public PhotoApiClient(Context context) {
        _context = context;
        _photoStorage = new PhotoStorage(_context);
    }


    public static String getCategoriesForYearUrl(int year) {
        return API_CATEGORIES_FOR_YEAR_URL + "/" + String.valueOf(year);
    }


    public static String getPhotosForCategoryUrl(int categoryId) {
        return API_PHOTOS_BY_CATEGORY_URL + "/" + String.valueOf(categoryId);
    }


    public static String getPhotosByCommentDateUrl(boolean newestFirst) {
        return API_PHOTOS_BY_COMMENT_DATE_URL + "/" + String.valueOf(newestFirst);
    }


    public static String getPhotosByUserCommentDateUrl(boolean newestFirst) {
        return API_PHOTOS_BY_USER_COMMENT_DATE_URL + "/" + String.valueOf(newestFirst);
    }


    public static String getPhotosByCommentCountUrl(boolean mostFirst) {
        return API_PHOTOS_BY_COMMENT_COUNT_URL + "/" + String.valueOf(mostFirst);
    }


    public static String getPhotosByAverageRatingUrl() {
        return API_PHOTOS_BY_AVERAGE_RATING_URL + "/true";
    }


    public static String getPhotosByUserRatingUrl() {
        return API_PHOTOS_BY_USER_RATING_URL + "/true";
    }


    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    public boolean isAuthenticated() {
        List<HttpCookie> cookies = _cookieManager.getCookieStore().getCookies();

        for (HttpCookie cookie : cookies) {
            if (AUTH_COOKIE_NAME.equalsIgnoreCase(cookie.getName()) && !cookie.hasExpired()) {
                return true;
            }
        }

        return false;
    }


    public boolean authenticate(String username, String password) {
        try {
            HttpRequestInfo info = new HttpRequestInfo();

            info.setUrl(new URL(API_LOGIN_URL));
            info.setMethod("POST");
            info.getParams().put("Username", username);
            info.getParams().put("Password", password);

            HttpResponseInfo response = execute(info);

            if (response != null && response.getStatusCode() == HttpURLConnection.HTTP_OK && Boolean.valueOf(response.getContent())) {
                // we actually do not care what the response is, to try and ensure we have successfully
                // logged in, we check for the existence of the auth ticket
                return isAuthenticated();
            }
        } catch (Exception ex) {
            Log.w(MawApplication.LOG_TAG, "Error when authenticating: " + ex.getMessage());
        }

        return false;
    }


    public List<Integer> getPhotoYears() throws MawAuthenticationException {
        ensureAuthenticated(false);

        String url = API_GET_PHOTO_YEARS_URL;
        JsonClient<Integer> jsonClient = new JsonClient<>(Integer.class, this);

        return jsonClient.getItemList(url);
    }


    public List<Category> getCategoriesForYear(int year) throws MawAuthenticationException {
        ensureAuthenticated(false);

        String url = getCategoriesForYearUrl(year);
        JsonClient<Category> jsonClient = new JsonClient<>(Category.class, this);

        return jsonClient.getItemList(url);
    }


    public List<Category> getRecentCategories(int sinceId) throws MawAuthenticationException {
        ensureAuthenticated(false);

        String url = API_GET_RECENT_CATEGORIES_URL + "/" + String.valueOf(sinceId);
        JsonClient<Category> jsonClient = new JsonClient<>(Category.class, this);

        return jsonClient.getItemList(url);
    }


    public int getTotalCategoryCount() throws MawAuthenticationException {
        ensureAuthenticated(false);

        JsonClient<Integer> jsonClient = new JsonClient<>(Integer.class, this);

        return jsonClient.getSingleItem(API_GET_CATEGORY_COUNT_URL);
    }


    public List<Photo> getPhotos(String url) throws MawAuthenticationException {
        ensureAuthenticated(false);

        JsonClient<Photo> jsonClient = new JsonClient<>(Photo.class, this);

        return jsonClient.getItemList(url);
    }


    public PhotoAndCategory getRandomPhoto() throws MawAuthenticationException {
        ensureAuthenticated(false);

        JsonClient<PhotoAndCategory> jsonClient = new JsonClient<>(PhotoAndCategory.class, this);

        return jsonClient.getSingleItem(API_GET_RANDOM_PHOTO_URL);
    }


    public ExifData getExifData(int photoId) throws MawAuthenticationException {
        ensureAuthenticated(false);

        String url = API_GET_PHOTO_EXIF_URL + "/" + String.valueOf(photoId);
        JsonClient<ExifData> jsonClient = new JsonClient<>(ExifData.class, this);

        return jsonClient.getSingleItem(url);
    }


    public List<Comment> getComments(int photoId) throws MawAuthenticationException {
        ensureAuthenticated(false);

        String url = API_GET_PHOTO_COMMENTS_URL + "/" + String.valueOf(photoId);
        JsonClient<Comment> jsonClient = new JsonClient<>(Comment.class, this);

        return jsonClient.getItemList(url);
    }


    public Rating getRatings(int photoId) throws MawAuthenticationException {
        ensureAuthenticated(false);

        String url = API_GET_PHOTO_RATING_URL + "/" + String.valueOf(photoId);
        JsonClient<Rating> jsonClient = new JsonClient<>(Rating.class, this);

        return jsonClient.getSingleItem(url);
    }


    public Float setRating(int photoId, int rating) throws MawAuthenticationException {
        ensureAuthenticated(false);

        RatePhoto rp = new RatePhoto();
        rp.setPhotoId(photoId);
        rp.setRating(rating);

        JsonClient<RatePhoto> jsonClient = new JsonClient<>(RatePhoto.class, this);

        String paramString = jsonClient.toJson(rp);

        try {
            URL url = new URL(API_SET_PHOTO_RATING_URL);

            HttpRequestInfo req = new HttpRequestInfo();
            req.setMethod("POST");
            req.setUrl(url);
            req.setJsonParam(paramString);

            HttpResponseInfo response = execute(req);

            if (response != null && response.getStatusCode() == HttpURLConnection.HTTP_OK) {
                return Float.valueOf(response.getContent());
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

        JsonClient<CommentPhoto> jsonClient = new JsonClient<>(CommentPhoto.class, this);

        String jsonParam = jsonClient.toJson(cp);

        try {
            URL url = new URL(API_ADD_PHOTO_COMMENT_URL);

            HttpRequestInfo req = new HttpRequestInfo();
            req.setMethod("POST");
            req.setUrl(url);
            req.setJsonParam(jsonParam);

            HttpResponseInfo response = execute(req);

            if (response != null && response.getStatusCode() == HttpURLConnection.HTTP_OK) {
                Log.w(MawApplication.LOG_TAG, "got response: " + response.getContent());
            } else {
                Log.w(MawApplication.LOG_TAG, "unable to save rating!");
            }
        } catch (MalformedURLException ex) {
            Log.e(MawApplication.LOG_TAG, "invalid url!");
        }
    }


    public boolean downloadPhoto(String photoPath) throws MawAuthenticationException {
        ensureAuthenticated(false);

        if (photoPath == null || TextUtils.isEmpty(photoPath)) {
            Log.w(MawApplication.LOG_TAG, "you need to specify the path to the photo");
        }

        // first try to get this from cache
        if (_photoStorage.doesExist(photoPath)) {
            return true;
        }

        HttpURLConnection conn = null;

        try {
            URL url = new URL(buildPhotoUrl(photoPath));

            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setRequestMethod("GET");

            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                if (!_isSecondAttempt) {
                    _isSecondAttempt = true;
                    ensureAuthenticated(true);

                    return downloadPhoto(photoPath);
                } else {
                    _isSecondAttempt = false; // reset
                }
            }

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                _photoStorage.put(photoPath, conn);

                _isSecondAttempt = false;

                return true;
            } else {
                Log.w(MawApplication.LOG_TAG, "Did not receive a successful download status code: " + String.valueOf(conn.getResponseCode()));
            }
        } catch (IOException ex) {
            Log.w(MawApplication.LOG_TAG, "Error when getting photo blob: " + ex.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        _isSecondAttempt = false;

        return false;
    }


    // this should only be called by methods that already expect to make network calls, otherwise android will complain
    // about this running on the ui thread
    void ensureAuthenticated(boolean force) throws MawAuthenticationException {
        if (force || !isAuthenticated()) {
            Log.d(MawApplication.LOG_TAG, "refreshing the authentication token");

            MawDataManager dm = new MawDataManager(_context);

            Credentials creds = dm.getCredentials();

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


    private HttpResponseInfo execute(HttpRequestInfo requestInfo) throws MawAuthenticationException {
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) requestInfo.getUrl().openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setRequestMethod(requestInfo.getMethod());

            if (requestInfo.getJsonParam() != null) {
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            conn.setDoInput(true);

            if (requestInfo.getParams().size() > 0 || requestInfo.getJsonParam() != null) {
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                if (requestInfo.getJsonParam() != null) {
                    writer.write(requestInfo.getJsonParam());
                } else {
                    writer.write(getQuery(requestInfo.getParams()));
                }

                writer.flush();
                writer.close();
                os.close();
            }

            conn.connect();

            // see if we are being told to redirect (in the event of session timeout), try again
            if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                if (!_isSecondAttempt) {
                    _isSecondAttempt = true;
                    ensureAuthenticated(true);

                    return execute(requestInfo);
                } else {
                    _isSecondAttempt = false; // reset
                }
            } else {
                _isSecondAttempt = false;
                return new HttpResponseInfo(conn.getResponseCode(), readStream(conn.getInputStream()));
            }
        } catch (IOException ex) {
            Log.w(MawApplication.LOG_TAG, "> error executing http request: " + ex.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }


    private String readStream(InputStream in) {
        char[] buf = new char[4096];
        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in);

        try {
            for (int len = reader.read(buf); len > 0; len = reader.read(buf)) {
                sb.append(buf, 0, len);
            }
        } catch (IOException ex) {
            Log.w(MawApplication.LOG_TAG, "> error reading stream: " + ex.getMessage());
        }

        return sb.toString();
    }


    private String getQuery(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Set<String> keys = params.keySet();

        for (String key : keys) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.get(key), "UTF-8"));
        }

        return result.toString();
    }
}
