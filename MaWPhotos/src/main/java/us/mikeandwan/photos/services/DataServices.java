package us.mikeandwan.photos.services;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;
import us.mikeandwan.photos.models.ApiCollection;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.models.CommentPhoto;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.models.FileOperationResult;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.models.Rating;


public class DataServices {
    private final DatabaseAccessor _databaseAccessor;
    private final PhotoApiClient _photoApiClient;
    private final PhotoStorage _photoStorage;
    private final BehaviorSubject<File[]> _fileQueueSubject;


    public DataServices(DatabaseAccessor databaseAccessor,
                        PhotoApiClient photoApiClient,
                        PhotoStorage photoStorage) {
        _databaseAccessor = databaseAccessor;
        _photoApiClient = photoApiClient;
        _photoStorage = photoStorage;

        File[] queuedFiles = _photoStorage.getQueuedFilesForUpload();

        _fileQueueSubject = BehaviorSubject.create();
        _fileQueueSubject.onNext(queuedFiles);
    }


    public ApiCollection<Comment> addComment(CommentPhoto cp) throws IOException {
        Timber.d("started to add comment for photo: %s", cp.getPhotoId());

        _photoApiClient.addComment(cp.getPhotoId(), cp.getComment());

        return _photoApiClient.getComments(cp.getPhotoId());
    }


    public String downloadCategoryTeaser(Category category) {
        Timber.d("started to download teaser for category: %s", category.getId());

        return downloadPhoto(category.getTeaserImage().getUrl());
    }


    public String downloadMdCategoryTeaser(Category category) {
        Timber.d("started to download md teaser for category: %s", category.getId());

        return downloadPhoto(category.getTeaserImage().getUrl().replace("/xs", "/md/"));
    }


    public String downloadPhoto(Photo photo, PhotoSize size) {
        Timber.d("started to download photo: %s", photo.getId());

        String path = null;

        switch (size) {
            case Sm:
                path = photo.getImageSm().getUrl();
                break;
            case Md:
                path = photo.getImageMd().getUrl();
                break;
            case Xs:
                path = photo.getImageXs().getUrl();
                break;
            case Lg:
                path = photo.getImageLg().getUrl();
                break;
        }

        return downloadPhoto(path);
    }


    public List<Category> getCategoriesForYear(int year) {
        Timber.d("started to get categories for year: %s", year);

        return _databaseAccessor.getCategoriesForYear(year);
    }


    public ApiCollection<Comment> getComments(int photoId) throws IOException {
        Timber.d("started to get comments for photo: %s", photoId);

        return _photoApiClient.getComments(photoId);
    }


    public ExifData getExifData(int photoId) throws Exception {
        Timber.d("started to get exif data for photo: %s", photoId);

        return _photoApiClient.getExifData(photoId);
    }


    public ApiCollection<Photo> getPhotoList(PhotoListType type, int categoryId) throws Exception {
        Timber.d("started to get photo list");

        return _photoApiClient.getPhotos(type, categoryId);
    }


    public List<Integer> getPhotoYears() {
        return _databaseAccessor.getPhotoYears();
    }


    public Photo getRandomPhoto() throws IOException {
        Timber.d("started to get random photo");

        return _photoApiClient.getRandomPhoto();
    }


    public ApiCollection<Photo> getRandomPhotos(int count) throws IOException {
        Timber.d("started to get random photos");

        return _photoApiClient.getRandomPhotos(count);
    }


    public Rating getRating(int photoId) throws IOException {
        Timber.d("started to get rating for photo: %s", photoId);

        return _photoApiClient.getRatings(photoId);
    }


    public ApiCollection<Category> getRecentCategories() throws IOException {
        Timber.d("started to get recent categories");

        ApiCollection<Category> categories = _photoApiClient.getRecentCategories(_databaseAccessor.getLatestCategoryId());

        _databaseAccessor.addCategories(categories.getItems());

        return categories;
    }


    public Uri getSharingContentUri(String remotePath) {
        return _photoStorage.getSharingContentUri(remotePath);
    }


    public Rating setRating(int photoId, int rating) throws IOException {
        Timber.d("started to set user rating for photo: %s", photoId);

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


    public Observable<File[]> getFileQueueObservable()
    {
        return _fileQueueSubject;
    }


    public boolean enequeFileToUpload(int id, InputStream inputStream, String mimeType) {
        boolean result = _photoStorage.enqueueFileToUpload(id, inputStream, mimeType);

        if(result) {
            updateQueuedFileSubject();
        }

        return result;
    }


    private void updateQueuedFileSubject() {
        _fileQueueSubject.onNext(_photoStorage.getQueuedFilesForUpload());
    }


    public void uploadQueuedFile(File file) throws Exception {
        try {
            FileOperationResult result = _photoApiClient.uploadFile(file);

            if(result.getWasSuccessful()) {
                _photoStorage.deleteFileToUpload(file);
                updateQueuedFileSubject();
            }
            else {
                String err = result.getError();

                // TODO: service to return error code
                if(err.contains("already exists")) {
                    _photoStorage.deleteFileToUpload(file);
                    updateQueuedFileSubject();
                } else {
                    Timber.e("error reported when uploading file: %s", err);

                    throw new Exception("Error uploading file " + file.getName());
                }
            }
        } catch (Exception ex) {
            Timber.e("error uploading file: %s", ex.getMessage());

            throw new Exception("Error uploading file " + file.getName());
        }
    }


    public void wipeTempFiles() { _photoStorage.wipeTempFiles(); }


    public void wipeCache() { _photoStorage.wipeCache(); }


    private String downloadPhoto(String path) {
        if(path == null || TextUtils.isEmpty(path)) {
            return _photoStorage.getPlaceholderThumbnail();
        }

        String cachePath = "file://" + _photoStorage.getCachePath(path);

        if (_photoStorage.doesExist(path)) {
            return cachePath;
        }
        else {
            try {
                Response response = _photoApiClient.downloadPhoto(path);

                if(response != null) {
                    if(response.isSuccessful()) {
                        _photoStorage.put(path, response.body());

                        ResponseBody body = response.body();

                        if(body != null) {
                            body.close();
                        }

                        response.close();

                        return cachePath;
                    }
                    else {
                        Timber.e("error downloading file [%s]: status code: %s", path, response.code());
                    }
                }
            }
            catch(Exception ex) {
                Timber.e("error downloading file [%s]: %s", path, ex.getMessage());
            }
        }

        return _photoStorage.getPlaceholderThumbnail();
    }
}
