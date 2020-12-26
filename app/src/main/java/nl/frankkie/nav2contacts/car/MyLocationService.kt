package nl.frankkie.nav2contacts.car

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import nl.frankkie.nav2contacts.MyApplication
import nl.frankkie.nav2contacts.R

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

    private fun startNotification() {
        val notification: Notification = Notification.Builder(this, getNotificationChannelId())
            .setContentTitle(getText(R.string.app_name))
            .setContentText(getText(R.string.app_name))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun getNotificationChannelId() : String {
        return MyApplication.NOTIFICATION_CHANNEL_ID
    }

    companion object {
        const val ONGOING_NOTIFICATION_ID = 1234
    }

}