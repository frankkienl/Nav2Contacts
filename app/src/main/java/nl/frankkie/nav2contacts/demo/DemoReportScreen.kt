package nl.frankkie.nav2contacts.demo

import android.graphics.*
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.model.*
import androidx.core.graphics.drawable.IconCompat
import nl.frankkie.nav2contacts.R

class DemoReportScreen(carContext: CarContext, val useGrid: Boolean = false) : Screen(carContext) {

    val useCloseButton = true

    override fun onGetTemplate(): Template {
        /*
        Pain point 1
        Even though GridTemplate and ListTemplate both extend from Template;
        Their builders are incompatible.
         */
        val templateBuilder1 =
            if (useGrid) {
                GridTemplate.Builder()
            } else {
                ListTemplate.Builder()
            }
        /*
           This means that Kotlin will cast it to Any;
           So you can't call .build()

           Commented to prevent compiling error:
         */
        //templateBuilder1.build()
        /*
            A possible solution could be to have them both extend a common Interface.
         */

        var actionStrip: ActionStrip? = null
        if (useCloseButton) {
            val actionStripBuilder = ActionStrip.Builder()
            actionStripBuilder.addAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext, R.drawable.reiger
                            )
                        ).build()
                    )
                    .setOnClickListener {
                        //close
                        carContext.getCarService(ScreenManager::class.java).popTo("ROOT")
                    }
                    .build()
            )
            actionStrip = actionStripBuilder.build()
        }


        //Because of the incompatibility, the fake inheritance, you end up with code-duplication.
        if (useGrid) {
            val templateBuilder = GridTemplate.Builder()
            templateBuilder.setHeaderAction(Action.BACK)
            templateBuilder.setTitle("Grid MaxSpeed report")
            templateBuilder.setSingleList(buildList(carContext))

            //Pain point;
            //Setting the ActionStrip to null should be allowed (compiling error)
            //Setting an empty ActionStrip should be allowed (runtime crash!!!!!!!!!!!!!)
            //Never should we have to expect runtime crashes for things like this!
            actionStrip?.let {
                templateBuilder.setActionStrip(it)
            }
            return templateBuilder.build()
        } else {
            val templateBuilder = ListTemplate.Builder()
            templateBuilder.setHeaderAction(Action.BACK)
            templateBuilder.setTitle("List MaxSpeed report")
            templateBuilder.setSingleList(buildList(carContext))
            actionStrip?.let {
                templateBuilder.setActionStrip(it)
            }
            return templateBuilder.build()
        }
    }

    private fun buildList(carContext: CarContext): ItemList {
        val itemListBuilder = ItemList.Builder()
        //Pain point,
        //We need more than 6 options. Automatic paging would be welcome!
        //Making the limit like 8 probably won't increase driver distraction very much.
        val speeds = arrayOf(15, 30, 50, 60, 70, 80 /* , 90, 100, 120, 130 */)
        for (speed in speeds) {
            itemListBuilder.addItem(buildItem(carContext, speed))
        }
        return itemListBuilder.build()
    }

    private fun buildItem(carContext: CarContext, speed: Int): Item {
        //Pain point.
        //Have to break out into separate paths as Row and GridItem are actually incompatible
        //Even though they both inherit from Item
        if (useGrid) {
            return buildGridItem(carContext, speed)
        } else {
            return buildRow(carContext, speed)
        }
    }

    private fun buildGridItem(carContext: CarContext, speed: Int): Item {
        val rowBuilder = GridItem.Builder()
        rowBuilder.setTitle("km/h")

        val image = buildSignImage(carContext, speed)

        rowBuilder.setImage(
            CarIcon.Builder(
                IconCompat.createWithBitmap(
                    image
                )
            ).build()
        )
        rowBuilder.setOnClickListener {
            clickedSpeedLimit(speed)
        }
        return rowBuilder.build()
    }

    private fun buildRow(carContext: CarContext, speed: Int): Item {
        //Pain point
        //Code duplication
        //This code is the same as above, but has to be repeated because
        //The builders of Row and GridItem are incompatible.
        val rowBuilder = Row.Builder()
        rowBuilder.setTitle("km/h")

        val image = buildSignImage(carContext, speed)

        rowBuilder.setImage(
            CarIcon.Builder(
                IconCompat.createWithBitmap(
                    image
                )
            ).build()
        )
        rowBuilder.setOnClickListener {
            clickedSpeedLimit(speed)
        }
        return rowBuilder.build()
    }

    private fun clickedSpeedLimit(speed: Int) {
        CarToast.makeText(carContext, "Clicked: $speed", CarToast.LENGTH_LONG).show()
    }

    private fun buildSignImage(carContext: CarContext, speed: Int): Bitmap {
        //Small pain point
        //Using the compose pattern discourages caching,
        //while images like this could be cached instead of recreated every .invalidate()
        //Have fun wasting cpu-cycles on garbage collection.
        val width = 64
        val height = 64
        val centerXSpeed = width / 2F
        val centerYSpeed = height / 2F
        val circleSize = 56.toFloat()
        val textSize = 28F
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val speedPaint = Paint().apply { isAntiAlias = true }
        //Colors (Nightmode)
        val backgroundColor = if (carContext.isDarkMode) {
            Color.BLACK
        } else {
            Color.WHITE
        }
        val speedTextColor = if (carContext.isDarkMode) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        // First draw the white background
        speedPaint.strokeWidth = 6f
        speedPaint.color = backgroundColor
        speedPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawCircle(
            centerXSpeed,
            centerYSpeed,
            circleSize / 2,
            speedPaint
        )
        // Then draw the red stroke
        speedPaint.color = Color.RED
        speedPaint.style = Paint.Style.STROKE
        canvas.drawCircle(
            centerXSpeed,
            centerYSpeed,
            circleSize / 2,
            speedPaint
        )
        // And finally draw the current speed text
        speedPaint.color = speedTextColor
        speedPaint.style = Paint.Style.FILL
        speedPaint.textAlign = Paint.Align.CENTER
        speedPaint.textSize = textSize
        speedPaint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(
            speed.toString(),
            centerXSpeed,
            centerYSpeed - ((speedPaint.descent() + speedPaint.ascent()) / 2),
            speedPaint
        )
        return bitmap
    }

}