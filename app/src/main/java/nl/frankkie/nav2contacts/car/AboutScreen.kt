package nl.frankkie.nav2contacts.car

import com.google.android.libraries.car.app.CarContext
import com.google.android.libraries.car.app.Screen
import com.google.android.libraries.car.app.model.Action
import com.google.android.libraries.car.app.model.CarIcon
import com.google.android.libraries.car.app.model.MessageTemplate
import com.google.android.libraries.car.app.model.Template
import nl.frankkie.nav2contacts.R

class AboutScreen(carContext: CarContext) : Screen(carContext) {
    override fun getTemplate(): Template {
        val messageTemplateBuilder = MessageTemplate.builder(carContext.getString(R.string.about_desc))
        messageTemplateBuilder.setTitle(carContext.getString(R.string.app_name))
        messageTemplateBuilder.setMessage(carContext.getString(R.string.about_desc))
        messageTemplateBuilder.setIcon(CarIcon.APP_ICON)
        messageTemplateBuilder.setHeaderAction(Action.BACK)
        return messageTemplateBuilder.build()
    }

}