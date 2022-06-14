package nl.frankkie.nav2contacts.car

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.car.app.CarContext
import androidx.core.content.ContextCompat

fun navigateToContactAddress(carContext: CarContext, address: MyContactAddress) {
    val intent = Intent(
        CarContext.ACTION_NAVIGATE,
        Uri.parse("geo:${address.latitude},${address.longitude}")
    )
    carContext.startCarApp(intent)
}

fun checkRequiredPermissions(context: Context): Boolean {
    //formatting is hard
    val contacts = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED

    val location = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return contacts && location
}

fun askRequiredPermissions(carContext: CarContext) {
    if (isAndroidAuto(carContext)) return //only for aaos
    //TODO: ask permissions for aaos
}

fun isAndroidAuto(context: Context) : Boolean {
    return !context.packageManager.hasSystemFeature("android.hardware.type.automotive")
}