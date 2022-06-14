package nl.frankkie.nav2contacts.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template

class MissingPermissionScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        //Permission error
        val templateBuilder = if (isAndroidAuto(carContext)) {
            MessageTemplate.Builder(carContext.getString(R.string.error_permissions_android_auto))
        } else {
            MessageTemplate.Builder(carContext.getString(R.string.error_permissions_aaos))
        }
        templateBuilder.apply {
            setHeaderAction(Action.APP_ICON)
            setTitle(carContext.getString(R.string.app_name))

            if (!isAndroidAuto(carContext)) {
                addAction(
                    Action.Builder()
                        .setTitle(carContext.getText(android.R.string.ok))
                        .setOnClickListener {
                            //ParkedOnlyOnClickListener.create {
                                askRequiredPermissions(
                                    carContext
                                )
                            //}
                        }
                        .build()
                )
            }

            addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.action_close_app))
                    .setOnClickListener { carContext.finishCarApp() }
                    .build()
            )
        }
        return templateBuilder.build()
    }
}