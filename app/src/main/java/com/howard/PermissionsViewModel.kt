package com.howard

import android.Manifest
import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel

class PermissionsViewModel(application: Application) : AndroidViewModel(application) {
    val userFacingStrings = buildMap {
        put(Manifest.permission.ACCESS_FINE_LOCATION, "Fine Location")
        put(Manifest.permission.ACCESS_COARSE_LOCATION, "Coarse Location")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            put(Manifest.permission.BLUETOOTH_SCAN, "BT Scan")
        } else {
            put(Manifest.permission.BLUETOOTH, "BT")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(Manifest.permission.ACCESS_BACKGROUND_LOCATION, "Location Always")
            put(Manifest.permission.ACTIVITY_RECOGNITION, "Physical Activity")
        }
    }
    val permissions = userFacingStrings.keys.toMutableList().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) remove(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }
}
