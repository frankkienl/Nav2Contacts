package nl.frankkie.nav2contacts.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template

class AboutScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val messageTemplateBuilder = MessageTemplate.Builder(carContext.getString(R.string.about_desc))
        messageTemplateBuilder.setTitle(carContext.getString(R.string.app_name))
        messageTemplateBuilder.setIcon(CarIcon.APP_ICON)
        messageTemplateBuilder.setHeaderAction(Action.BACK)
        return messageTemplateBuilder.build()
    }
}