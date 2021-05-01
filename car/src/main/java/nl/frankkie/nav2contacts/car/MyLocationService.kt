package nl.frankkie.nav2contacts.car

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder

class MyLocationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification: Notification = Notification.Builder(this, getNotificationChannelId())
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
            startForeground(ONGOING_NOTIFICATION_ID, notification)
        } else {
            //val notification: Notification = Notification.Builder(this)
            //TODO("VERSION.SDK_INT < O")
        }
    }

    private fun getNotificationChannelId(): String {
        return nl.frankkie.nav2contacts.common.NOTIFICATION_CHANNEL_ID
    }

    companion object {
        const val ONGOING_NOTIFICATION_ID = 1234
    }

}