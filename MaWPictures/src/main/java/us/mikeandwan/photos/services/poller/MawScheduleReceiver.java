package us.mikeandwan.photos.services.poller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;


public class MawScheduleReceiver extends BroadcastReceiver {
    @Inject SharedPreferences _sharedPrefs;



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MawApplication.LOG_TAG, "> scheduleReceiver onReceive - scheduling poller");

        ((MawApplication) context.getApplicationContext()).getApplicationComponent().inject(this);

        schedule(context);
    }


    private void schedule(Context context) {
        int repeatHours = Integer.valueOf(_sharedPrefs.getString("sync_frequency", "24"));  // odd, prefs only work when string based..

        schedule(context, repeatHours);
    }


    public void schedule(Context context, int repeatInHours) {
        long repeatIntervalInMillis = repeatInHours * AlarmManager.INTERVAL_HOUR;
        //long repeatIntervalInMillis = 20000;

        Intent i = new Intent(context, MawStartReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (repeatIntervalInMillis < 0) {
            // user has disabled background checking
            service.cancel(pending);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 15);

            service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), repeatIntervalInMillis, pending);
        }
    }
}
