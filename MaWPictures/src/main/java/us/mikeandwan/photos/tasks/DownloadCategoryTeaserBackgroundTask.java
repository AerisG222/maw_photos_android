package us.mikeandwan.photos.tasks;

import android.content.Context;
import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.widget.CategoryRowDetail;


public class DownloadCategoryTeaserBackgroundTask extends BackgroundTask<CategoryRowDetail> {
    private Context _context;
    private CategoryRowDetail _rowDetail;


    public DownloadCategoryTeaserBackgroundTask(Context context, CategoryRowDetail rowDetail) {
        super(BackgroundTaskPriority.Normal);

        _context = context;
        _rowDetail = rowDetail;
    }


    @Override
    public CategoryRowDetail call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to download teaser for category: " + _rowDetail.getCategory().getId());

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        client.downloadPhoto(_rowDetail.getCategory().getTeaserPhotoInfo().getPath());

        return _rowDetail;
    }
}
