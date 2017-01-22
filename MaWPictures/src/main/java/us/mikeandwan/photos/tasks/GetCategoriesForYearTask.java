package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetCategoriesForYearTask {
    private MawDataManager _dm;
    private PhotoApiClient _client;


    @Inject
    public GetCategoriesForYearTask(MawDataManager dataManager, PhotoApiClient client) {
        _dm = dataManager;
        _client = client;
    }


    public List<Category> call(int year) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started GetCategoriesForYear(" + year + ")");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        List<Category> categories = _client.getCategoriesForYear(year);
        List<Category> cachedCategories = _dm.getCategoriesForYear(year);

        if (categories.size() > 0) {
            categories.removeAll(cachedCategories);

            for (Category category : categories) {
                _dm.addCategory(category);
            }
        }

        Log.d(MawApplication.LOG_TAG, "> completed GetCategoriesForYear(" + year + ")");

        return _dm.getCategoriesForYear(year);
    }
}
