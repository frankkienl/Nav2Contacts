package nl.frankkie.nav2contacts.car

import com.google.android.libraries.car.app.CarContext
import com.google.android.libraries.car.app.Screen
import com.google.android.libraries.car.app.model.Action
import com.google.android.libraries.car.app.model.MessageTemplate
import com.google.android.libraries.car.app.model.Template
import nl.frankkie.nav2contacts.R

class ErrorScreen(carContext: CarContext) : Screen(carContext) {
    override fun getTemplate(): Template {
        //Permission error
        return MessageTemplate.builder(carContext.getString(R.string.error_permissions))
            .setHeaderAction(Action.APP_ICON)
            .setTitle(carContext.getString(R.string.app_name))
            .build()
    }

}