# Nav2Contacts
Navigate to contacts, for Android Auto  
With this app will search for addresses in your contacts, so make sure your contacts are up to date.  
This app will need contacts permission and location permission.  
No user data will ever leave your phone.

## Demo video
Demo video: https://github.com/frankkienl/Nav2Contacts/blob/master/Demo-Nav2Contacts.mp4  

## How to install
Download APK-file: https://github.com/frankkienl/Nav2Contacts/blob/master/Nav2Contacts-release.apk  

Perform the following ADB-commands to install the app:  
```
adb push Nav2Contacts-release.apk /data/local/tmp/app.apk
adb shell pm install -i "com.android.vending" -r /data/local/tmp/app.apk
adb shell rm /data/local/tmp/app.apk
```

## First use
Before using the app for the first time, grant the contacts and location permission.
This can only be done on the phone, not via the Android Auto interface.

## How to use
1) On Android Auto, start the navigation app you intent to use.  
E.g. Google Maps, Flitsmeister, Sygic.  
(This is needed as the navigation will be started in the most recently used navigation app)
  
2) Search for a contact.  
Note: You can only use the keyboard when the car is parked!  
A maximum of 6 search results can be shown at a time.  
When there is no search-query entered, it will show some 'starred' contacts.  
You can star/unstar contacts via the contact-app on your phone.
  
3) Select the correct address.  
(Contacts can have multiple addresses)
  
4) Click `navigate`.  
This will start navigation in you most recently used navigation app.
