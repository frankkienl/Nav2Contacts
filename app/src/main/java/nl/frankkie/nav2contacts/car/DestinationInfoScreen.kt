package nl.frankkie.nav2contacts.car

import com.google.android.libraries.car.app.CarContext
import com.google.android.libraries.car.app.Screen
import com.google.android.libraries.car.app.model.*
import nl.frankkie.nav2contacts.R
import nl.frankkie.nav2contacts.navigateToContactAddress

class DestinationInfoScreen(
    carContext: CarContext,
    private val contact: MyContact,
    private val contactAddress: MyContactAddress
) : Screen(carContext) {

    override fun getTemplate(): Template {
        val template = MessageTemplate.builder(carContext.getString(R.string.navigate_to) + " ${contact.name}\n${contactAddress.street}, ${contactAddress.city}")
        template.setHeaderAction(Action.BACK)
        template.setIcon(CarIcon.APP_ICON)
        template.setActions(buildActions(carContext, contact, contactAddress))
        return template.build()
    }

    private fun buildActions(
        carContext: CarContext,
        contact: MyContact,
        contactAddress: MyContactAddress
    ): List<Action> {
        val actionBuilder = Action.builder()
            .setBackgroundColor(CarColor.GREEN)
            .setTitle(carContext.getString(R.string.navigate))
            .setOnClickListener {
                navigateToContactAddress(carContext, contactAddress)
            }
        return listOf(actionBuilder.build())
    }

}