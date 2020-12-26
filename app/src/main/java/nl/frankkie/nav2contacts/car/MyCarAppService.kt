package nl.frankkie.nav2contacts.car

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.libraries.car.app.CarAppService
import com.google.android.libraries.car.app.Screen


class MyCarAppService : CarAppService() {
    override fun onCreateScreen(intent: Intent): Screen {

        return HomeScreen(carContext)
    }
}