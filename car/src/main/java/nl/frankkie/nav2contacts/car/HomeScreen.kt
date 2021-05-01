package nl.frankkie.nav2contacts.car

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.model.*


class HomeScreen(carContext: CarContext) : Screen(carContext) {

    private val myObserver = object : DefaultLifecycleObserver {
        @SuppressLint("MissingPermission")
        override fun onCreate(owner: LifecycleOwner) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(carContext)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                currentLocation = location
                invalidate()
            }
            fusedLocationClient.lastLocation.addOnFailureListener {
                Log.e("N2C", "NOMS", it)
            }
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 5 * 1000
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(carContext)
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (location != null) {
                    currentLocation = location
                }
            }
            invalidate()
        }
    }

    init {
        lifecycle.addObserver(myObserver)
    }

    var currentLocation: Location? = null

    override fun onGetTemplate(): Template {
        val placeListMapTemplateBuilder = PlaceListMapTemplate.Builder()
        placeListMapTemplateBuilder.setTitle(carContext.getString(R.string.app_name))
        placeListMapTemplateBuilder.setHeaderAction(Action.APP_ICON)
        placeListMapTemplateBuilder.setCurrentLocationEnabled(true)

        //current location
        currentLocation?.let {
            val place = Place.Builder(CarLocation.create(it)).build()
            placeListMapTemplateBuilder.setAnchor(place)
        }

        //Set menu items
        val itemListBuilder = ItemList.Builder()
//        itemListBuilder.addItem(
//            Row.Builder()
//                .setIsBrowsable(true)
//                .setTitle(carContext.getString(R.string.favorites))
//                .setOnClickListener { clickedFavorites() }
//                .build()
//        )
//        itemListBuilder.addItem(
//            Row.Builder()
//                .setIsBrowsable(true)
//                .setTitle(carContext.getString(R.string.nearby))
//                .setOnClickListener { clickedNearby() }
//                .build()
//        )
        itemListBuilder.addItem(
            Row.Builder()
                .setBrowsable(true)
                .setTitle(carContext.getString(R.string.search))
                .setOnClickListener { clickedSearch() }
                .build()
        )
        itemListBuilder.addItem(
            Row.Builder()
                .setBrowsable(true)
                .setTitle(carContext.getString(R.string.about))
                .setOnClickListener { clickedAbout() }
                .build()
        )
        placeListMapTemplateBuilder.setItemList(itemListBuilder.build())

        return placeListMapTemplateBuilder.build()
    }

    private fun clickedFavorites() {
        val sc = carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
        sc.push(NamesOnMapScreen(carContext, NamesOnMapScreen.ACTION_FAVORITES))
    }

    private fun clickedNearby() {
        val sc = carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
        sc.push(NamesOnMapScreen(carContext, NamesOnMapScreen.ACTION_NEARBY))
    }

    private fun clickedSearch() {
        val sc = carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
        sc.push(SearchScreen(carContext))
    }

    private fun clickedAbout() {
        val sc = carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
        sc.push(AboutScreen(carContext))
    }


    private fun checkPermissions(): Boolean {
        //formatting is hard
        val contacts = ContextCompat.checkSelfPermission(
            carContext,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        val location = ContextCompat.checkSelfPermission(
            carContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return contacts && location
    }

}