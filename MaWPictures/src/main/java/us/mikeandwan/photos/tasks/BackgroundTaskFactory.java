package us.mikeandwan.photos.tasks;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import us.mikeandwan.photos.data.CommentPhoto;
import us.mikeandwan.photos.data.Credentials;
import us.mikeandwan.photos.data.MawDataManager;
import us.mikeandwan.photos.data.PhotoDownload;
import us.mikeandwan.photos.data.PhotoSize;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.widget.CategoryRowDetail;


@EBean(scope = EBean.Scope.Singleton)
public class BackgroundTaskFactory {
    @Bean
    MawDataManager _dataManager;

    @Bean
    PhotoApiClient _client;


    public AddCommentBackgroundTask BuildAddCommentBackgroundTask(CommentPhoto cp) {
        return new AddCommentBackgroundTask(_client, cp);
    }


    public DownloadCategoryTeaserBackgroundTask BuildDownloadCategoryTeaserBackgroundTask(CategoryRowDetail detail) {
        return new DownloadCategoryTeaserBackgroundTask(_client, detail);
    }


    public DownloadImageBackgroundTask BuildDownloadImageBackgroundTask(PhotoDownload photoDownload, PhotoSize size, BackgroundTaskPriority priority) {
        return new DownloadImageBackgroundTask(_client, photoDownload, size, priority);
    }


    public GetCategoriesForYearBackgroundTask BuildGetCategoriesForYearBackgroundTask(int year) {
        return new GetCategoriesForYearBackgroundTask(_dataManager, _client, year);
    }


    public GetCommentsBackgroundTask BuildGetCommentsBackgroundTask(int photoId) {
        return new GetCommentsBackgroundTask(_client, photoId);
    }


    public GetExifDataBackgroundTask BuildGetExifDataBackgroundTask(int photoId) {
        return new GetExifDataBackgroundTask(_client, photoId);
    }


    public GetPhotoListBackgroundTask BuildGetPhotoListBackgroundTask(String url) {
        return new GetPhotoListBackgroundTask(_client, url);
    }


    public GetRandomPhotoBackgroundTask BuildGetRandomPhotoBackgroundTask() {
        return new GetRandomPhotoBackgroundTask(_client);
    }


    public GetRatingBackgroundTask BuildGetRatingBackgroundTask(int photoId) {
        return new GetRatingBackgroundTask(_client, photoId);
    }


    public GetRecentCategoriesBackgroundTask BuildGetRecentCategoriesBackgroundTask() {
        return new GetRecentCategoriesBackgroundTask(_dataManager, _client);
    }


    public GetYearsBackgroundTask BuildGetYearsBackgroundTask() {
        return new GetYearsBackgroundTask(_dataManager, _client);
    }


    public LoginBackgroundTask BuildLoginBackgroundTask(Credentials credentials) {
        return new LoginBackgroundTask(_client, credentials);
    }


    public SetRatingBackgroundTask BuildSetRatingBackgroundTask(int photoId, int rating) {
        return new SetRatingBackgroundTask(_client, photoId, rating);
    }
}
