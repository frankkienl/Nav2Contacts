package nl.frankkie.nav2contacts.car

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.util.Log
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
import nl.frankkie.nav2contacts.getDistanceFromLatLonInKm
import nl.frankkie.nav2contacts.getLatLngFromAddress


/**
 * When given a contactId:
 * Shows a map with the addresses of a contact
 * else
 * Shows a map with favorites
 */
class NamesOnMapScreen(
    carContext: CarContext,
    private val action: Int,
    private val contact: MyContact? = null
) :
    Screen(carContext) {

    private val myObserver = object : DefaultLifecycleObserver {
        @SuppressLint("MissingPermission")
        override fun onCreate(owner: LifecycleOwner) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(carContext)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                currentLocation = location
                invalidate()
            }
            fusedLocationClient.lastLocation.addOnFailureListener {
                Log.e("N2C", "fusedLocationClient onFailure", it)
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

    val handler = Handler()
    val geocoder = Geocoder(carContext)
    var geocodingDone = false
    var currentLocation: Location? = null
    var currentSelectedIndex = -1

    init {
        lifecycle.addObserver(myObserver)

        if (action == ACTION_CONTACT) {
            //geocode all addresses
            Thread().run {
                contact?.addresses?.forEach { myContactAddress ->
                    val latLng = getLatLngFromAddress(
                        geocoder,
                        myContactAddress.street,
                        myContactAddress.city,
                        myContactAddress.country
                    )
                    latLng?.let {
                        myContactAddress.latitude = it[0]
                        myContactAddress.longitude = it[1]
                    }
                }
                geocodingDone = true
                handler.post {
                    invalidate()
                }
            }
        }
    }


    override fun onGetTemplate(): Template {
        val placeListMapTemplate = PlaceListMapTemplate.Builder()
        placeListMapTemplate.setHeaderAction(Action.BACK)
        placeListMapTemplate.setCurrentLocationEnabled(true)


        if (currentLocation == null) {
            if (action == ACTION_CONTACT) {
                contact?.let {
                    placeListMapTemplate.setTitle(it.name)
                }
            }
            placeListMapTemplate.setLoading(true)
            return placeListMapTemplate.build()
        }

        currentLocation?.let {
            placeListMapTemplate.setAnchor(Place.Builder(CarLocation.create(it)).build())
        }

        if (action == ACTION_CONTACT) {
            if (contact == null || !geocodingDone) {
                placeListMapTemplate.setLoading(true)
                return placeListMapTemplate.build()
            }

            placeListMapTemplate.setTitle(contact.name)
            val itemListBuilder = buildItemList(contact.addresses)
            placeListMapTemplate.setItemList(itemListBuilder.build())
        }

        return placeListMapTemplate.build()
    }


    private fun buildItemList(contactAddresses: ArrayList<MyContactAddress>): ItemList.Builder {
        val itemListBuilder = ItemList.Builder()
        if (!geocodingDone) {
            //No geocode, no nothing.
            return itemListBuilder
        }

        var contactAddressesLabel = 1
        contactAddresses.forEach { address ->

            val rowBuilder = Row.Builder()

            //Title
            val addressString = "${address.street}, ${address.city}"
            rowBuilder.setTitle(addressString)

            //Metadata
            val placeMarker = PlaceMarker.Builder().setColor(CarColor.GREEN)
                .setLabel(contactAddressesLabel++.toString()).build()
            val placeBuilder = Place.Builder(
                CarLocation.create(
                    address.latitude ?: 0.0,
                    address.longitude ?: 0.0
                )
            )
            placeBuilder.setMarker(placeMarker)
            val metadataBuilder =  Metadata.Builder()
            metadataBuilder.setPlace(placeBuilder.build())
            rowBuilder.setMetadata(
               metadataBuilder.build()
            )

            //Distance
            val interpunct = "·"
            val distanceInKm = getDistanceFromLatLonInKm(
                currentLocation?.latitude ?: 0.0,
                currentLocation?.longitude ?: 0.0,
                address.latitude ?: 0.0,
                address.longitude ?: 0.0
            )
            val distanceSpan =
                DistanceSpan.create(
                    Distance.create(
                        distanceInKm,
                        Distance.UNIT_KILOMETERS
                    )
                )
            val string =
                SpannableString("  $interpunct " + carContext.getString(R.string.distance))
            string.setSpan(
                distanceSpan, 0, 1, SPAN_INCLUSIVE_INCLUSIVE
            )
            rowBuilder.addText(string)
            rowBuilder.setOnClickListener {
                //show the destination detail screen
                val screenManager = carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
                screenManager.push(DestinationInfoScreen(carContext, contact!!, address))
            }

            itemListBuilder.addItem(rowBuilder.build())
        }

        return itemListBuilder
    }

    companion object {
        const val ACTION_FAVORITES = 1
        const val ACTION_NEARBY = 2
        const val ACTION_CONTACT = 3
    }
}