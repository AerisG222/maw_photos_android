package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetRecentCategoriesTask {
    private MawDataManager _dm;
    private PhotoApiClient _client;


    @Inject
    public GetRecentCategoriesTask(MawDataManager dataManager, PhotoApiClient client) {
        _dm = dataManager;
        _client = client;
    }


    public List<Category> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get recent categories");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        List<Category> categories = _client.getRecentCategories(_dm.getLatestCategoryId());

        _dm.addCategories(categories);

        return categories;
    }
}
