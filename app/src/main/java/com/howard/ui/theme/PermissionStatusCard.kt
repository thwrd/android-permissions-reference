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
    val userFacingStrings = mapOf(
        Manifest.permission.ACCESS_FINE_LOCATION to "Location (Fine)",
        Manifest.permission.ACCESS_COARSE_LOCATION to "Location (Coarse)",
        Manifest.permission.ACTIVITY_RECOGNITION to "Physical Activity",
        Manifest.permission.BLUETOOTH_SCAN to "BT Scan (Nearby)",
        Manifest.permission.BLUETOOTH to "BT"
    )

    val permissionsState = rememberMultiplePermissionsState(
        permissions = buildList {
            addAll(
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) add(Manifest.permission.ACTIVITY_RECOGNITION)
            add(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Manifest.permission.BLUETOOTH_SCAN
                } else {
                    Manifest.permission.BLUETOOTH
                }
            )
        })

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

            permissionsState.permissions.forEachIndexed { index, permissionState ->
                PermissionStatusText(
                    index = index,
                    permissionName = userFacingStrings[permissionState.permission] ?: "",
                    multiplePermissionsState = permissionsState
                )
            }

            allTheTimePermission?.let {
                PermissionStatusText(status = it.status, permissionName = "Location Always")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionStatusText(
    index: Int,
    permissionName: String,
    multiplePermissionsState: MultiplePermissionsState
) {
    val status = multiplePermissionsState.permissions[index].status
    PermissionStatusText(status, permissionName)
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun PermissionStatusText(
    status: PermissionStatus,
    permissionName: String
) {
    val text = when {
        status == PermissionStatus.Granted -> "$permissionName: Granted ✔"
        status.shouldShowRationale -> "$permissionName: Denied (Rationale) ⚠️"
        else -> "$permissionName: Denied or Not Requested ❌"
    }
    Text(
        text = text,
        fontSize = 16.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}