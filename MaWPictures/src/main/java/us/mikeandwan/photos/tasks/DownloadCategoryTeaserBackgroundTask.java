package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.widget.CategoryRowDetail;


public class DownloadCategoryTeaserBackgroundTask extends BackgroundTask<CategoryRowDetail> {
    private final CategoryRowDetail _rowDetail;
    private final PhotoApiClient _client;


    public DownloadCategoryTeaserBackgroundTask(PhotoApiClient client, CategoryRowDetail rowDetail) {
        super(BackgroundTaskPriority.Normal);

        _client = client;
        _rowDetail = rowDetail;
    }


    @Override
    public CategoryRowDetail call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to download teaser for category: " + _rowDetail.getCategory().getId());

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        _client.downloadPhoto(_rowDetail.getCategory().getTeaserPhotoInfo().getPath());

        return _rowDetail;
    }
}
