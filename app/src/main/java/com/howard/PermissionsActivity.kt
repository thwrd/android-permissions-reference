package com.howard

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

const val HOME = 0
const val ALL_AT_ONCE = 1
const val SEQUENTIAL = 2

class PermissionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TabbedLayout() }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsRequestScreen() {
    val permissionsList = buildList {
        addAll(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            } else {
                listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                )
            }
        )
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) add(Manifest.permission.ACTIVITY_RECOGNITION)
    }
    val multiplePermissionsState = rememberMultiplePermissionsState(permissionsList)

    when {
        multiplePermissionsState.allPermissionsGranted -> Text("All permissions granted!")
        multiplePermissionsState.shouldShowRationale -> PermissionRationaleUI {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }

        else -> RequestPermissionsUI {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }
    }
}

@Composable
fun PermissionRationaleUI(onRequestPermissions: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("We need Bluetooth and Location access to connect to nearby devices.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermissions) {
            Text("Request Permissions")
        }
    }
}

@Composable
fun RequestPermissionsUI(onRequestPermissions: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Please grant Bluetooth and Location permissions.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermissions) {
            Text("Grant Permissions")
        }
    }
}


@Composable
fun TabbedLayout() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Front Load", "Sequential")

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            HOME -> HomeTab()
            ALL_AT_ONCE -> PermissionsRequestScreen()
            SEQUENTIAL -> RequestPermissionsPage("p", "q") {}

            /*
            the CTA text for all at once should show, if any should show permission rationale
             */
        }
    }
}