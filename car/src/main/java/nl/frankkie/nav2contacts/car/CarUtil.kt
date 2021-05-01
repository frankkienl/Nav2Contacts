package nl.frankkie.nav2contacts.car

import android.content.Intent
import android.net.Uri
import androidx.car.app.CarContext

fun navigateToContactAddress(carContext: CarContext, address: MyContactAddress) {
    val intent = Intent(
        CarContext.ACTION_NAVIGATE,
        Uri.parse("geo:${address.latitude},${address.longitude}")
    )
    carContext.startCarApp(intent)
}