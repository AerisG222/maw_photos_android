package us.mikeandwan.photos.services.poller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.prefs.SyncPreference;


public class MawScheduleReceiver extends BroadcastReceiver {
    @Inject SyncPreference _syncPrefs;


    @Override
    public void onReceive(Context context, Intent intent) {
        if(!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(MawApplication.LOG_TAG, "scheduleReceiver onReceive: did *NOT* match the BOOT COMPLETED event - aborting");
            return;
        }

        Log.d(MawApplication.LOG_TAG, "scheduleReceiver onReceive - scheduling poller");

        ((MawApplication) context.getApplicationContext()).getApplicationComponent().inject(this);

        schedule(context);
    }


    private void schedule(Context context) {
        schedule(context, _syncPrefs.getSyncFrequencyInHours());
    }


    public void schedule(Context context, int repeatInHours) {
        //long repeatIntervalInMillis = repeatInHours * AlarmManager.INTERVAL_HOUR;
        long repeatIntervalInMillis = 20000;

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
