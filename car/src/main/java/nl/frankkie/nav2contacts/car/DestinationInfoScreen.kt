package nl.frankkie.nav2contacts.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*

class DestinationInfoScreen(
    carContext: CarContext,
    private val contact: MyContact,
    private val contactAddress: MyContactAddress
) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val templateBuilder =
            MessageTemplate.Builder(carContext.getString(R.string.navigate_to) + " ${contact.name}\n${contactAddress.street}, ${contactAddress.city}")
        templateBuilder.setHeaderAction(Action.BACK)
        templateBuilder.setIcon(CarIcon.APP_ICON)
        buildAction(carContext, contact, contactAddress, templateBuilder)
        return templateBuilder.build()
    }

    private fun buildAction(
        carContext: CarContext,
        contact: MyContact,
        contactAddress: MyContactAddress,
        templateBuilder: MessageTemplate.Builder
    ) {
        val actionBuilder = Action.Builder()
            .setBackgroundColor(CarColor.GREEN)
            .setTitle(carContext.getString(R.string.navigate))
            .setOnClickListener {
                navigateToContactAddress(carContext, contactAddress)
            }
        templateBuilder.addAction(actionBuilder.build())
    }

}