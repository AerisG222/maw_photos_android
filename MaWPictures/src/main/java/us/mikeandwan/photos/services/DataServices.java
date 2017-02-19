package us.mikeandwan.photos.services;

import android.net.Uri;
import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.models.CommentPhoto;
import us.mikeandwan.photos.models.Credentials;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.models.Rating;


public class DataServices {
    private final DatabaseAccessor _databaseAccessor;
    private final PhotoApiClient _photoApiClient;
    private final PhotoStorage _photoStorage;


    public DataServices(DatabaseAccessor databaseAccessor,
                        PhotoApiClient photoApiClient,
                        PhotoStorage photoStorage) {
        _databaseAccessor = databaseAccessor;
        _photoApiClient = photoApiClient;
        _photoStorage = photoStorage;
    }


    public List<Comment> addComment(CommentPhoto cp) throws Exception {
        Log.d(MawApplication.LOG_TAG, "started to add comment for photo: " + cp.getPhotoId());

        _photoApiClient.addComment(cp.getPhotoId(), cp.getComment());

        return _photoApiClient.getComments(cp.getPhotoId());
    }


    public Boolean authenticate(Credentials creds) throws Exception {
        Log.d(MawApplication.LOG_TAG, "started to authenticate");

        if (_photoApiClient.authenticate(creds.getUsername(), creds.getPassword())) {
            Log.i(MawApplication.LOG_TAG, "authenticated");

            return true;
        } else {
            Log.i(MawApplication.LOG_TAG, "authentication failed");

            return false;
        }
    }


    public String downloadCategoryTeaser(Category category) {
        Log.d(MawApplication.LOG_TAG, "started to download teaser for category: " + category.getId());

        return downloadPhoto(category.getTeaserPhotoInfo().getPath());
    }


    public String downloadPhoto(Photo photo, PhotoSize size) {
        Log.d(MawApplication.LOG_TAG, "started to download photo: " + photo.getId());

        String path = null;

        switch (size) {
            case Sm:
                path = photo.getSmInfo().getPath();
                break;
            case Md:
                path = photo.getMdInfo().getPath();
                break;
            case Xs:
                path = photo.getXsInfo().getPath();
                break;
            case Lg:
                path = photo.getLgInfo().getPath();
                break;
        }

        return downloadPhoto(path);
    }


    public List<Category> getCategoriesForYear(int year) {
        Log.d(MawApplication.LOG_TAG, "started to get categories for year: " + year);

        return _databaseAccessor.getCategoriesForYear(year);
    }


    public List<Comment> getComments(int photoId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "started to get comments for photo: " + photoId);

        return _photoApiClient.getComments(photoId);
    }


    public Credentials getCredentials() {
        return _databaseAccessor.getCredentials();
    }


    public ExifData getExifData(int photoId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "started to get exif data for photo: " + photoId);

        return _photoApiClient.getExifData(photoId);
    }


    public List<Photo> getPhotoList(PhotoListType type, int categoryId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "started to get photo list");

        return _photoApiClient.getPhotos(type, categoryId);
    }


    public List<Integer> getPhotoYears() {
        return _databaseAccessor.getPhotoYears();
    }


    public PhotoAndCategory getRandomPhoto() throws Exception {
        Log.d(MawApplication.LOG_TAG, "started to get random photo");

        return _photoApiClient.getRandomPhoto();
    }


    public Rating getRating(int photoId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "started to get rating for photo: " + photoId);

        return _photoApiClient.getRatings(photoId);
    }


    public List<Category> getRecentCategories() throws Exception {
        Log.d(MawApplication.LOG_TAG, "started to get recent categories");

        List<Category> categories = _photoApiClient.getRecentCategories(_databaseAccessor.getLatestCategoryId());

        _databaseAccessor.addCategories(categories);

        return categories;
    }


    public Uri getSharingContentUri(String remotePath) {
        return _photoStorage.getSharingContentUri(remotePath);
    }


    public void setCredentials(Credentials credentials) {
        _databaseAccessor.setCredentials(credentials);
    }


    public Rating setRating(int photoId, int rating) throws Exception {
        Log.d(MawApplication.LOG_TAG, "started to set user rating for photo: " + photoId);

        Float averageRating = _photoApiClient.setRating(photoId, rating);

        Rating rate = new Rating();

        if (averageRating != null) {
            rate.setAverageRating(averageRating);
            rate.setUserRating((short) Math.round(rating));
        } else {
            rate.setAverageRating((float) 0);
            rate.setUserRating((short) 0);
        }

        return rate;
    }


    public void wipeLegacyCache() {
        _photoStorage.wipeLegacyCache();
    }


    private String downloadPhoto(String path) {
        String cachePath = "file://" + _photoStorage.getCachePath(path);

        if (_photoStorage.doesExist(path)) {
            return cachePath;
        }
        else {
            try {
                if(_photoApiClient.downloadPhoto(path)) {
                    return cachePath;
                }
            }
            catch(Exception ex) {
                Log.e(MawApplication.LOG_TAG, "error downloading file [" + path + "]: " + ex.getMessage());
            }
        }

        return _photoStorage.getPlaceholderThumbnail();
    }
}
