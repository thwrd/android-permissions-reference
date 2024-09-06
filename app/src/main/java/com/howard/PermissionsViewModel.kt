package com.howard

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel

typealias RequiredPermission = Pair<String, PermissionState>

enum class PermissionState { NOT_GRANTED, SHOW_RATIONALE }

class PermissionsViewModel(private val application: Application) : AndroidViewModel(application) {
    val requiredPermission = mutableStateOf<RequiredPermission?>(null)

    @get:RequiresApi(Build.VERSION_CODES.S)
    val isScanGranted get() = isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN)

    @get:RequiresApi(Build.VERSION_CODES.S)
    val isConnectGranted get() = isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)

    @get:RequiresApi(Build.VERSION_CODES.S)
    val isFineLocationGranted get() = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.S)
    val android11AndUpPermissionsList = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            application.applicationContext, permission
        ) == PackageManager.PERMISSION_GRANTED

    val androidUpTo10PermissionsList =
        listOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }

    fun arePermissionsGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

}
