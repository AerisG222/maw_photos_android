package us.mikeandwan.photos.tasks;

import android.content.Context;
import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetYearsBackgroundTask extends BackgroundTask<List<Integer>> {
    private MawDataManager _dm;
    private Context _context;


    public GetYearsBackgroundTask(Context context) {
        super(BackgroundTaskPriority.High);

        _context = context;
        _dm = new MawDataManager(_context);
    }


    @Override
    public List<Integer> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started GetYears()");
        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        List<Integer> years = client.getPhotoYears();
        List<Integer> cachedYears = _dm.getPhotoYears();

        if (years.size() > 0) {
            years.removeAll(cachedYears);

            for (Integer year : years) {
                _dm.addYear(year);
            }
        }

        Log.d(MawApplication.LOG_TAG, "> completed GetYears()");

        // now return the properly ordered years from our local cache
        return _dm.getPhotoYears();
    }
}
