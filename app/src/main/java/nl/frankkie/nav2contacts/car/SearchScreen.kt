package nl.frankkie.nav2contacts.car

import android.provider.ContactsContract
import androidx.core.graphics.drawable.IconCompat
import androidx.preference.PreferenceManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.model.*
import nl.frankkie.nav2contacts.R

class SearchScreen(carContext: CarContext) : Screen(carContext) {

    var searchResults: ArrayList<MyContact>? = null

    private val searchListener = object : SearchTemplate.SearchCallback {
        override fun onSearchTextChanged(searchText: String) {
            searchContacts(searchText)
        }

        override fun onSearchSubmitted(searchText: String) {
            searchContacts(searchText)
        }

    }

    override fun onGetTemplate(): Template {
        val searchTemplate = SearchTemplate.Builder(searchListener)
        searchTemplate.setShowKeyboardByDefault(false)
        searchTemplate.setInitialSearchText("")
        searchTemplate.setHeaderAction(Action.BACK)
        searchTemplate.setSearchHint(carContext.getString(R.string.search_contact))
        if (searchResults == null) {
            searchContacts("") //try to get list of favorites
        }
        searchResults?.let {
            if (it.isNotEmpty()) {
                searchTemplate.setItemList(buildItemList())
            }
        }
        searchTemplate.setActionStrip(buildActionStrip())
        return searchTemplate.build()
    }

    private fun buildActionStrip(): ActionStrip {
        val actionStripBuilder = ActionStrip.Builder()
        actionStripBuilder.addAction(
            Action.Builder()
                .setIcon(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_settings
                        )
                    ).build()
                )
                .setOnClickListener {
                    goToSettingsScreen()
                }
                .build()
        )
        return actionStripBuilder.build()
    }

    private fun goToSettingsScreen() {
        val screenManager =
            carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
        screenManager.push(SettingsScreen(carContext))
    }

    private fun buildItemList(): ItemList {
        val builder = ItemList.Builder()
        if (searchResults == null) {
            searchContacts("")
        }
        searchResults?.let { safeSearchResults ->
            safeSearchResults.map {
                val rowBuilder = Row.Builder()
                rowBuilder.setTitle(it.name)
                rowBuilder.addText(
                    carContext.resources.getQuantityString(
                        R.plurals.x_addresses_found,
                        it.addresses.size,
                        it.addresses.size
                    )
                )
                if (it.starred) {
                    rowBuilder.setImage(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_star
                            )
                        ).build()
                    )
                } else {
                    rowBuilder.setImage(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_star_outline
                            )
                        ).build()
                    )
                }
                rowBuilder.setOnClickListener { clickedContact(it) }

                builder.addItem(rowBuilder.build())
            }
        }
        return builder.build()
    }

    private fun clickedContact(contact: MyContact) {
        val sc = carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
        sc.push(NamesOnMapScreen(carContext, NamesOnMapScreen.ACTION_CONTACT, contact))
    }

    private fun searchContacts(searchText: String) {
        val cursor =
            if (searchText.isNotBlank()) {
                carContext.contentResolver.query(
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.StructuredPostal._ID,
                        ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.StructuredPostal.STARRED,
                        ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                        ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY
                    ),
                    ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME + " like ?",
                    arrayOf("%$searchText%"),
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC"
                )
            } else {
                carContext.contentResolver.query(
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.StructuredPostal._ID,
                        ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.StructuredPostal.STARRED,
                        ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                        ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY
                    ),
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC"
                )
            }
        cursor?.let { safeCursor ->
            val tempSearchResults = arrayListOf<MyContact>()
            val starredOnlyFilter = if (searchText.isNotBlank()) {
                PreferenceManager.getDefaultSharedPreferences(carContext)
                    .getBoolean(SettingsScreen.PREF_FAVORITES_ONLY, false)
            } else {
                true
            }
            while (cursor.moveToNext()) {

                //Contact
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME))
                        ?: ""
                val starred =
                    (cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STARRED)) == 1)
                val tempContact = MyContact(name, starred)

                if (starredOnlyFilter) {
                    if (!starred) {
                        continue //skip if not starred
                    }
                }

                //Address
                val street =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET))
                        ?: ""
                val city =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY))
                        ?: ""
                val country =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY))
                        ?: ""

                val tempMyContactAddress = MyContactAddress(street, city, country)

                if (tempSearchResults.contains(tempContact)) {
                    val tempTempContact = tempSearchResults[tempSearchResults.indexOf(tempContact)]
                    if (!tempTempContact.addresses.contains(tempMyContactAddress)) {
                        tempTempContact.addresses.add(
                            tempMyContactAddress
                        )
                    }
                } else {
                    tempContact.addresses.add(tempMyContactAddress)
                    tempSearchResults.add(tempContact)
                }
            }
            safeCursor.close()
            //max 6
            if (searchResults == null) {
                searchResults = arrayListOf()
            }
            searchResults?.let { safeSearchResults ->
                safeSearchResults.clear()
                tempSearchResults.forEach { myContact ->
                    if (safeSearchResults.size < 6) {
                        safeSearchResults.add(myContact)
                    }
                }
            }
        }
        invalidate()
    }
}

