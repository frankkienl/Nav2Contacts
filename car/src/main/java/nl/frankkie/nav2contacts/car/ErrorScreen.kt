package nl.frankkie.nav2contacts.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template

class ErrorScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        //Permission error
        return MessageTemplate.Builder(carContext.getString(R.string.error_permissions))
            .setHeaderAction(Action.APP_ICON)
            .setTitle(carContext.getString(R.string.app_name))
            .build()
    }
}