package nl.frankkie.nav2contacts.demo

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.model.*
import androidx.core.graphics.drawable.IconCompat
import nl.frankkie.nav2contacts.R

class DemoScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val templateBuilder = ListTemplate.Builder()
        templateBuilder.setHeaderAction(Action.BACK)
        templateBuilder.setTitle("Demo")

        /*
        Some pain points...
         1) This code looks like a mess / complex.
         Too much indents / function calls in a row, make code look complex.
         2) If you remove one of the ".build()"'s,
         the code lights up like an alarm.
         And it's unclear what's wrong and where the missing .build() should go exactly
         3) Why must the CarIcon be a builder at all?
         The IconCompat is not a builder either.

         //
         Some proposed ideas/solutions:
         1) Allow property access syntax (instead of setters) in Kotlin;
         .setTitle("") --> .title = ""
         This reduces the number of characters per line, and making it look less complex.
         2) Allow the functions to take both the Item and the Item.Builder as input
         This can be done with Kotlin extensions. KTX library.
         See the awesome extension function below.
         3) Allow things like CarIcon to be just a static function,
         or give us access to the real model classes directly.
         I know you do this to make them "final" and not "reusable",
         that's part of the Compose-pattern,
         but this doesn't really prevent that anyway and is just annoying.

         None of these pain points are deal-breakers though.
         Can wait for next version of Android Auto.
         */
        templateBuilder.setSingleList(
            ItemList.Builder()
                .addItem(
                    Row.Builder()
                        .setTitle("Report List demo")
                        .setBrowsable(true)
                        .setImage(
                            CarIcon.Builder(
                                IconCompat.createWithResource(
                                    carContext,
                                    R.drawable.reiger
                                )
                            ).build()
                        )
                        .setOnClickListener { clickedReportDemo(false) }
                        .build()
                )
                .addItem(
                    Row.Builder()
                        .setTitle("Report Grid demo")
                        .setBrowsable(true)
                        .setImage(
                            CarIcon.Builder(
                                IconCompat.createWithResource(
                                    carContext,
                                    R.drawable.reiger
                                )
                            ).build()
                        )
                        .setOnClickListener { clickedReportDemo(true) }
                        .build()
                )
                .build() //Can you tell in a second, where this .build() belongs too?
        )

        return templateBuilder.build()
    }

    fun clickedReportDemo(useGrid: Boolean) {
        //Pain point: It shouldn't be needed to do a cast here
        val sm = carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
        sm.push(DemoReportScreen(carContext, useGrid))
    }

}

//Awesome extension function that removes a ".build()" from your code
fun ItemList.Builder.addItem(itemBuilder: Row.Builder): ItemList.Builder {
    this.addItem(itemBuilder.build())
    return this
}