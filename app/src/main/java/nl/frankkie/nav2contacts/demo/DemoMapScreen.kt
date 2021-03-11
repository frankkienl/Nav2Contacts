package nl.frankkie.nav2contacts.demo

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.model.*
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.navigation.model.TravelEstimate
import androidx.core.graphics.drawable.IconCompat
import nl.frankkie.nav2contacts.R
import java.util.*

class DemoMapScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val templateBuilder = NavigationTemplate.Builder()
        templateBuilder.setDestinationTravelEstimate(buildETA(carContext))

        templateBuilder.setActionStrip(
            ActionStrip.Builder().addAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(carContext, R.drawable.reiger)
                        ).build()
                    )
                    .setOnClickListener { clickedReiger() }
                    .build()
            ).build()
        )

        return templateBuilder.build()
    }

    private fun clickedReiger(){
        val sm = carContext.getCarService(ScreenManager::class.java) //no cast :-)
        sm.pop()
    }

    fun buildETA(carContext: CarContext): TravelEstimate {
        val distance = Distance.create(1000.toDouble(), Distance.UNIT_KILOMETERS) //demo value
        var durationInSeconds = 100.toLong() //demo value
        if (durationInSeconds <= 0) { //fix crash
            durationInSeconds = 0
        }

        //TODO: Check if this works in other timezones...
        val dateTimeWithZone = DateTimeWithZone.create(
            System.currentTimeMillis() + (durationInSeconds * 1000),
            TimeZone.getDefault()
        )

        val travelEstimate = TravelEstimate.Builder(
            distance,
            dateTimeWithZone
        ).apply {
            setRemainingTimeSeconds(durationInSeconds)
        }
        val percentageDelay = 2
        /*
        Pain point!
        https://issuetracker.google.com/issues/174584571
        We should be able to use a custom color here;
        Our color even had more contrast than the Google-supplied color.
        The docs say custom colors will be check for contrast, but that's false.
        Custom colors are just denied everywhere except for 3 (!!) places

        Proposed solution:
        Actually check the contrast. Code is available in DemoColorContrastUtil.kt.


         */
//        val carColorForDelay = when {
//            percentageDelay >= 25 -> CarColor.createCustom(
//                Color.RED,  //Pretend this is a custom color for now
//                Color.RED
//            )
//            percentageDelay >= 5 -> CarColor.createCustom(
//                Color.YELLOW,
//                Color.YELLOW
//            )
//            else -> CarColor.createCustom(
//                Color.GREEN,
//                Color.GREEN
//            )
//        }
        // We're not allowed to use customs colors here !
        val carColorForDelay = when {
            percentageDelay >= 25 -> CarColor.RED
            percentageDelay >= 5 -> CarColor.YELLOW
            else -> CarColor.GREEN
        }
        travelEstimate.setRemainingTimeColor(carColorForDelay)
        return travelEstimate.build()
    }
}