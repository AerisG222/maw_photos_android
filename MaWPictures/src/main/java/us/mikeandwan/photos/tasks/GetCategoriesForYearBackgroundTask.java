package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Category;
import us.mikeandwan.photos.data.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetCategoriesForYearBackgroundTask extends BackgroundTask<List<Category>> {
    private final int _year;
    private final MawDataManager _dm;
    private final PhotoApiClient _client;


    public GetCategoriesForYearBackgroundTask(MawDataManager dataManager, PhotoApiClient client, int year) {
        super(BackgroundTaskPriority.Normal);

        _dm = dataManager;
        _client = client;
        _year = year;
    }


    @Override
    public List<Category> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started GetCategoriesForYear(" + _year + ")");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        List<Category> categories = _client.getCategoriesForYear(_year);
        List<Category> cachedCategories = _dm.getCategoriesForYear(_year);

        if (categories.size() > 0) {
            categories.removeAll(cachedCategories);

            for (Category category : categories) {
                _dm.addCategory(category);
            }
        }

        Log.d(MawApplication.LOG_TAG, "> completed GetCategoriesForYear(" + _year + ")");

        return _dm.getCategoriesForYear(_year);
    }
}
