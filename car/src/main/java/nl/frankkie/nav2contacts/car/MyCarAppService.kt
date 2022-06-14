package nl.frankkie.nav2contacts.car

import android.content.Intent
import android.content.res.Configuration
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator


class MyCarAppService : CarAppService() {

    private val session = object : Session() {
        override fun onCreateScreen(intent: Intent): Screen {
            return if (checkRequiredPermissions(carContext)) {
                HomeScreen(carContext)
            } else {
                MissingPermissionScreen(carContext)
            }
        }

        override fun onCarConfigurationChanged(newConfiguration: Configuration) {
        }

        override fun onNewIntent(intent: Intent) {
        }
    }

    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return session
    }
}