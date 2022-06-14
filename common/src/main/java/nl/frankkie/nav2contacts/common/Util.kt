package nl.frankkie.nav2contacts.common

import android.content.Context
import android.location.Geocoder
import android.util.Log
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun getLatLngFromAddress(
    context: Context,
    street: String,
    city: String,
    country: String
): Array<Double>? {
    return getLatLngFromAddress(Geocoder(context), street, city, country)
}

fun getLatLngFromAddress(
    geocoder: Geocoder,
    street: String,
    city: String,
    country: String
): Array<Double>? {
    try {
        val fromLocationName = geocoder.getFromLocationName("$street, $city, $country", 1)
        if (fromLocationName.isNotEmpty()) {
            return arrayOf(fromLocationName[0].latitude, fromLocationName[0].longitude)
        }
    } catch (e: Exception) {
        Log.e("Nav2Contact", "Geocoder error", e)
    }
    return null
}

fun getDistanceFromLatLonInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    //https://stackoverflow.com/a/27943/1398449
    val R = 6371; // Radius of the earth in km
    val dLat = deg2rad(lat2 - lat1)  // deg2rad below
    val dLon = deg2rad(lon2 - lon1)
    val a =
        sin(dLat / 2) * sin(dLat / 2) +
                cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val d = R * c; // Distance in km
    return d
}

fun deg2rad(deg: Double): Double {
    //https://stackoverflow.com/a/27943/1398449
    return deg * (Math.PI / 180)
}

