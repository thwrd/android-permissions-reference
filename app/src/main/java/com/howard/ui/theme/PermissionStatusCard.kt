package com.howard.ui.theme

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsStatusCard() {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BODY_SENSORS,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Manifest.permission.BLUETOOTH_SCAN
            } else {
                Manifest.permission.BLUETOOTH
            }
        )
    )

    val allTheTimePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberPermissionState(permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else {
        null
    }
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Permissions Status",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PermissionStatusText(0, "Location (Fine)", permissionsState)
            PermissionStatusText(1, "Location (Coarse)", permissionsState)
            PermissionStatusText(2, "Physical Activity", permissionsState)
            PermissionStatusText(3, "Nearby Devices", permissionsState)

            allTheTimePermission?.let {
                val text = when {
                    it.status == PermissionStatus.Granted -> "Location Always: Allowed ✔"
                    it.status.shouldShowRationale -> "Location Always: Denied (Rationale) ❌"
                    else -> "Location Always: Denied or Not Requested ❌"
                }
                Text(text = text, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionStatusText(
    index: Int,
    permissionName: String,
    permissionState: MultiplePermissionsState
) {
    val status = permissionState.permissions[index].status
    val text = when {
        status == PermissionStatus.Granted -> "$permissionName: Granted ✔"
        status.shouldShowRationale -> "$permissionName: Denied (Rationale) ❌"
        else -> "$permissionName: Denied or Not Requested ❌"
    }
    Text(
        text = text,
        fontSize = 16.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}