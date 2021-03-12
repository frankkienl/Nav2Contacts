package nl.frankkie.nav2contacts.demo

import androidx.annotation.ColorInt
import androidx.car.app.model.CarColor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object CarColorContrastUtil {

    /**
     * Check if the contrast between 2 colors is enough
     * Inspired by:
     * https://stackoverflow.com/a/9733420/1398449
     * https://www.w3.org/TR/WCAG20-TECHS/G17.html#G17-procedure
     */
    fun carColorsCheckEnoughContrast(
        backgroundColor: CarColor,
        foregroundColor: CarColor,
        darkMode: Boolean
    ): Boolean {
        return colorsCheckEnoughContrast(
            if (!darkMode) {
                backgroundColor.color
            } else {
                backgroundColor.colorDark
            },
            if (!darkMode) {
                foregroundColor.color
            } else {
                foregroundColor.colorDark
            },
            false
        )
    }

    fun colorsContrastRatio(@ColorInt color1: Int, @ColorInt color2: Int): Double {
        return colorsContrastRatio(colorLuminance(color1), colorLuminance(color2))
    }

    fun colorsContrastRatio(luminance1: Double, luminance2: Double): Double {
        val brightest = max(luminance1, luminance2)
        val darkest = min(luminance1, luminance2)
        val contrast = (brightest + 0.05) / (darkest + 0.05)
        return contrast
    }

    fun colorsCheckEnoughContrast(color1: Int, color2: Int, largeText: Boolean): Boolean {
        val contrast = colorsContrastRatio(colorLuminance(color1), colorLuminance(color2))
        return if (!largeText) {
            contrast > CONTRAST_LIMIT
        } else {
            contrast > CONTRAST_LIMIT_LARGE
        }
    }

    fun colorLuminance(@ColorInt color: Int): Double {
        val red = colorComponentLuminance(red(color))
        val green = colorComponentLuminance(green(color))
        val blue = colorComponentLuminance(blue(color))
        return (red * 0.2126) + (green * 0.7152) + (blue * 0.0722)
    }

    fun colorComponentLuminance(colorComponent: Int): Double {
        val component = colorComponent.toDouble() / 255.toDouble()
        return if (component <= 0.03928) {
            (component / 12.92)
        } else {
            ((component + 0.055) / 1.055).pow(2.4)
        }
    }

    //added because Color.red didn't work in Unit-test
    fun red(@ColorInt color: Int): Int {
        return (color shr 16) and 0xFF
    }

    //added because Color.green didn't work in Unit-test
    fun green(@ColorInt color: Int): Int {
        return (color shr 8) and 0xFF
    }

    //added because Color.blue didn't work in Unit-test
    fun blue(@ColorInt color: Int): Int {
        return color and 0xFF
    }

    const val CONTRAST_LIMIT = 4.5 //normal text
    const val CONTRAST_LIMIT_LARGE = 3 //large text
}
