package com.howard

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class PermissionsActivity : ComponentActivity() {
    val viewModel: PermissionsViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            viewModel.permissionsGranted.value = isGranted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    viewModel.permissionsGranted.value = isGranted
                }
            )
            PermissionsApp(viewModel, requestPermissionLauncher)
        }
    }
}

@Composable
fun PermissionsApp(
    viewModel: PermissionsViewModel,
    permissionLauncher: ActivityResultLauncher<String>
) {
    val showRationale by viewModel.showRationale
    val permissionsGranted by viewModel.permissionsGranted

    when {
        permissionsGranted -> PermissionsGrantedUI()
        showRationale -> PermissionRationaleUI { permissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN) }
        else -> RequestPermissionsUI { permissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN) }
    }
}

@Composable
fun PermissionsGrantedUI() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { Text("Permissions Granted!") }
}

@Composable
fun PermissionRationaleUI(onRequestPermissions: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You need Bluetooth and Location for full features.")
        Button(onClick = onRequestPermissions) { Text("Request Permissions") }
    }
}

@Composable
fun RequestPermissionsUI(onRequestPermissions: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Grant permissions for Bluetooth and Location.")
        Button(onClick = onRequestPermissions) { Text("Request Permissions") }
    }
}