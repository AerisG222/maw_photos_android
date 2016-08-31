package us.mikeandwan.photos.tasks;

import android.content.Context;
import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Category;
import us.mikeandwan.photos.data.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetCategoriesForYearBackgroundTask extends BackgroundTask<List<Category>> {
    private final MawDataManager _dm;
    private final Context _context;
    private final int _year;


    public GetCategoriesForYearBackgroundTask(Context context, int year) {
        super(BackgroundTaskPriority.Normal);

        _context = context;
        _year = year;
        _dm = new MawDataManager(_context);
    }


    @Override
    public List<Category> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started GetCategoriesForYear(" + _year + ")");

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        List<Category> categories = client.getCategoriesForYear(_year);
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
