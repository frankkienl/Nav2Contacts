package nl.frankkie.nav2contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            refreshContacts()
        }

    }

    private fun askPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1337
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            //open app settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            Toast.makeText(this, "Please enable 'Read contacts' permission.", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun refreshContacts() {
        if ((ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED)
            ||
            (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            askPermission()
            return
        }
    }

    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, MyApplication.NOTIFICATION_CHANNEL_ID)
        }
        startActivity(intent)
    }

}