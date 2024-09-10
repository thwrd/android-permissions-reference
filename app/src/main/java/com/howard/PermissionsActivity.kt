package com.howard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

const val HOME = 0
const val ALL_AT_ONCE = 1
const val SEQUENTIAL = 2

class PermissionsActivity : ComponentActivity() {
    private val viewModel: PermissionsViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val goToSettingsAction = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
            var selectedTabIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf("Home", "Front Load", "Sequential")
            val permissionsState = rememberMultiplePermissionsState(viewModel.permissions)
            val text = remember {
                mutableStateOf(
                    "To add a new One-Key item, insert the battery\n" +
                            "and pull the trigger."
                )
            }
            val buttonText = remember { mutableStateOf("") }
            val action: MutableState<() -> Unit> = remember {
                mutableStateOf({})
            }
            text.value =
                if (permissionsState.allPermissionsGranted) "To add a new One-Key item, insert the battery\n" +
                        "and pull the trigger." else
                    "[You must all access to Bluetooth, Location Services, and [other] in order to unlock the full suite of features that One-Key® offers.]"

            action.value = when {
                permissionsState.allPermissionsGranted -> {
                    {
                    }
                }

                permissionsState.shouldShowRationale -> {
                    { permissionsState.launchMultiplePermissionRequest() }
                }


                else -> {
                    { goToSettingsAction() }
                }
            }

            buttonText.value = when {
                permissionsState.allPermissionsGranted -> "Need Help?"
                permissionsState.shouldShowRationale -> "Request Permissions"
                else -> "Go to Settings"
            }

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
                    HOME -> HomeTab(
                        viewModel.userFacingStrings,
                        viewModel.permissions,
                        goToSettingsAction
                    )

                    ALL_AT_ONCE -> RequestPermissionsPage(
                        explanation = text.value,
                        ctaText = buttonText.value,
                        ctaAction = action.value
                    ) { permissionsState.launchMultiplePermissionRequest() }

                    SEQUENTIAL -> RequestPermissionsPage(text.value, "q", action.value)
                }
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsRequestScreen(permissions: List<String>) {
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)

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