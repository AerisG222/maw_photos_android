package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetYearsTask {
    private MawDataManager _dm;
    private PhotoApiClient _client;


    @Inject
    public GetYearsTask(MawDataManager dataManager, PhotoApiClient client) {
        _dm = dataManager;
        _client = client;
    }


    public List<Integer> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started GetYears()");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        List<Integer> years = _client.getPhotoYears();
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
