package us.mikeandwan.photos.tasks;

import android.util.Log;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.PhotoApiClient;


public class DownloadCategoryTeaserTask {
    private PhotoApiClient _client;


    @Inject
    public DownloadCategoryTeaserTask(PhotoApiClient client) {
        _client = client;
    }


    public boolean call(Category category) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to download teaser for category: " + category.getId());

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        _client.downloadPhoto(category.getTeaserPhotoInfo().getPath());

        return true;
    }
}
