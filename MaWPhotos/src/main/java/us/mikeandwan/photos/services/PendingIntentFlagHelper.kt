package us.mikeandwan.photos.services

import android.app.PendingIntent
import android.os.Build

class PendingIntentFlagHelper {
    companion object {
        fun getMutableFlag(baseFlags: Int): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                baseFlags or PendingIntent.FLAG_MUTABLE
            } else {
                baseFlags
            }
        }
    }
}