package nl.frankkie.nav2contacts.aaos_fake_contacts

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentProviderOperation
import android.content.DialogInterface
import android.content.OperationApplicationException
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat


/**
 * Please forgive the quality of this code.
 * Deadlines and stuff, you know the drill.
 */

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setContentView(R.layout.activity_main)
        val btnGo = findViewById<Button>(R.id.btn_go)
        btnGo.setOnClickListener { go() }
        val btnRead = findViewById<Button>(R.id.btn_read)
        btnRead.setOnClickListener { read() }
    }

    private fun hasPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            //Done
            return true
        }
        //No permission, request.
        requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS
            ), 1337
        )
        return false
    }

    /**
     * Some random addresses in the area around the Googleplex;
     * Why? Because otherwise you have to change the fake location in the Emulator.
     */
    private fun go() {
        //Starbucks
        val starbucks = MyContact(
            "[FAKE] Starbucks", true, arrayListOf(
                MyContactAddress("1380 Pear Ave", "Mountain View", "United States"),
                MyContactAddress("2410 Charleston Rd", "Mountain View", "United States"),
                MyContactAddress("580 N Rengstorff Ave", "Mountain View", "United States")
            )
        )
        insertFakeContact(starbucks)
        //7-Eleven
        val sevenEleven = MyContact(
            "[FAKE] Seven Eleven", false, arrayListOf(
                MyContactAddress("1380 Pear Ave", "Mountain View", "United States"),
                MyContactAddress("1951 Old Middlefield Way", "Mountain View", "United States"),
                MyContactAddress("2640 California St", "Mountain View", "United States")
            )
        )
        insertFakeContact(sevenEleven)
        //Pizza
        val pizza = MyContact(
            "[FAKE] Pizza Delivery", false, arrayListOf(
                MyContactAddress("327 Moffett Blvd", "Mountain View", "United States")
            )
        )
        insertFakeContact(pizza)
    }

    private fun insertFakeContact(someContact: MyContact) {
        //https://stackoverflow.com/a/18972011/1398449
        val ops = arrayListOf<ContentProviderOperation>()
        var rawContactInsertIndex = ops.size //WHY?! This makes no sense!!
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )
        //Phone Number
        //555-0100 through 555-0199 are now specifically reserved for fictional use
        //Source: Wikipedia - https://en.wikipedia.org/wiki/555_(telephone_number)
        val fakePhoneNumber = 5550100 + (Math.random() * 100)
        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                    ContactsContract.Data.RAW_CONTACT_ID,
                    rawContactInsertIndex
                )
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "$fakePhoneNumber")
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "1").build()
        )
        //Display name/Contact name
        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                    ContactsContract.Data.RAW_CONTACT_ID,
                    rawContactInsertIndex
                )
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    someContact.name
                )
                .build()
        )


        //Postal Address
        someContact.addresses.forEach { someAddress ->

            ops.add(
                ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex
                    )
                    .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POBOX, "Postbox")

                    .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                        someAddress.street
                    )

                    .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                        someAddress.city
                    )

                    .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, "region")

                    .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                        "postcode"
                    )

                    .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                        someAddress.country
                    )

                    .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, "3")


                    .build()
            )
        }

        try {
            val res = contentResolver.applyBatch(
                ContactsContract.AUTHORITY, ops
            )
            Log.v("AAA", "$res")
        } catch (e: RemoteException) {
            e.printStackTrace()
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
        }
    }

    private fun read() {
        val hasPermission = hasPermissions()
        if (!hasPermission) {
            Toast.makeText(this, "No permission", Toast.LENGTH_LONG).show()
            return
        }

        val cursor = contentResolver.query(
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
        var searchResults: ArrayList<MyContact>? = null
        cursor?.let { safeCursor ->
            val tempSearchResults = arrayListOf<MyContact>()
            val starredOnlyFilter = false
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
            if (searchResults == null) {
                searchResults = arrayListOf()
            }
            searchResults?.let { safeSearchResults ->
                safeSearchResults.clear()
                tempSearchResults.forEach { myContact ->
                    safeSearchResults.add(myContact)
                }
            }
            //Present results
            val sb = StringBuilder()
            searchResults?.forEach {
                sb.append("$it\n\n")
            }
            AlertDialog.Builder(this)
                .setTitle("Contacts")
                .setMessage(sb.toString())
                .setNeutralButton("OK", DialogInterface.OnClickListener { d, _ ->
                    try {
                        d.dismiss()
                    } catch (e: Exception) {
                    }
                })
                .create()
                .show()
        }
    }

}