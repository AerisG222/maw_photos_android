package us.mikeandwan.photos.poller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import us.mikeandwan.photos.MawApplication;


public class MawStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MawApplication.LOG_TAG, "> startReceiver onReceive - starting poller service");

        Intent service = new Intent(context, MawPollerService.class);
        context.startService(service);
    }
}
