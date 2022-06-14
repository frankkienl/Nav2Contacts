package nl.frankkie.nav2contacts.car

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.ScreenManager
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

fun navigateToContactAddress(carContext: CarContext, address: MyContactAddress) {
    val intent = Intent(
        CarContext.ACTION_NAVIGATE,
        Uri.parse("geo:${address.latitude},${address.longitude}?q=${address.street}, ${address.city}, ${address.country}")
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
    //Ask permissions for aaos
    val requiredPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_CONTACTS,
    )
    val myExcecutor = Executor { command -> command?.run() }
    carContext.requestPermissions(
        requiredPermissions,
        myExcecutor
    ) { approved, rejected ->
        if (approved.containsAll(requiredPermissions)) {
            CarToast.makeText(carContext, "Permissions granted", CarToast.LENGTH_LONG).show()

            val sc = carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
            sc.push(HomeScreen(carContext))
        }
    }
}

fun isAndroidAuto(context: Context): Boolean {
    return !context.packageManager.hasSystemFeature("android.hardware.type.automotive")
}