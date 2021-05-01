package nl.frankkie.nav2contacts

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //Create notification channel
        nl.frankkie.nav2contacts.common.initNotificationChannels(this)
    }
}