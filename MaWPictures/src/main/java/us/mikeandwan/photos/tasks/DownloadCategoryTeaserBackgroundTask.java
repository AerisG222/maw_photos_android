package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.models.ui.CategoryRowDetail;


public class DownloadCategoryTeaserBackgroundTask {
    @Bean
    PhotoApiClient _client;


    public CategoryRowDetail call(CategoryRowDetail rowDetail) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to download teaser for category: " + rowDetail.getCategory().getId());

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        _client.downloadPhoto(rowDetail.getCategory().getTeaserPhotoInfo().getPath());

        return rowDetail;
    }
}
