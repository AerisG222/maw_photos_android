package us.mikeandwan.photos.poller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.EReceiver;

import us.mikeandwan.photos.MawApplication;


@EReceiver
public class MawStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MawApplication.LOG_TAG, "> startReceiver onReceive - starting poller service");

        Intent service = new Intent(context, MawPollerService_.class);
        context.startService(service);
    }
}
