package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetRecentCategoriesBackgroundTask {
    @Bean
    MawDataManager _dm;

    @Bean
    PhotoApiClient _client;


    public List<Category> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get recent categories");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        List<Category> categories = _client.getRecentCategories(_dm.getLatestCategoryId());

        for (Category category : categories) {
            _dm.addCategory(category);
        }

        // new check which should cover case if the initial download was interrupted / failed
        int serverCount = _client.getTotalCategoryCount();
        int localCount = _dm.getCategoryCount();

        if (serverCount != localCount) {
            Log.w(MawApplication.LOG_TAG, "> different number of categories on server, attempting full refresh");

            List<Category> serverCategories = _client.getRecentCategories(0);
            List<Category> localCategories = _dm.getAllCategories();

            serverCategories.removeAll(localCategories);

            for (Category cat : serverCategories) {
                // add this category to the database
                _dm.addCategory(cat);

                // also add this category to the list of categories we report as being new
                categories.add(cat);
            }

            Log.w(MawApplication.LOG_TAG, "> completed full refresh");
        }

        return categories;
    }
}
