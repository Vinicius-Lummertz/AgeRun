package com.exemplo.agerun.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.location.hasLocationPermission
import com.exemplo.agerun.location.rememberDeviceLocation
import com.exemplo.agerun.model.BottomModule
import com.exemplo.agerun.state.AgeRunAppState
import com.exemplo.agerun.ui.components.BottomModuleBar
import com.exemplo.agerun.ui.components.FloatingActionIcon

@Composable
fun CoachPanelScreen(
    modifier: Modifier = Modifier,
    appState: AgeRunAppState,
) {
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        hasLocationPermission = context.hasLocationPermission()
    }
    val currentLocation = rememberDeviceLocation(
        hasPermission = hasLocationPermission,
        context = context,
    )

    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(18.dp))
                when {
                    appState.selectedModule == BottomModule.Students && appState.selectedStudent != null -> {
                        StudentProfileScreen(appState = appState)
                    }
                    appState.selectedModule == BottomModule.Home -> {
                        DashboardHomeScreen(
                            appState = appState,
                            currentLocation = currentLocation,
                            hasLocationPermission = hasLocationPermission,
                            onRequestLocationPermission = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                    ),
                                )
                            },
                        )
                    }
                    appState.selectedModule == BottomModule.Students -> {
                        StudentsModuleScreen(appState = appState)
                    }
                    appState.selectedModule == BottomModule.Workouts -> {
                        WorkoutsModuleScreen(appState = appState)
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            BottomModuleBar(
                selectedModule = appState.selectedModule,
                onModuleSelected = appState::selectModule,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            )
        }

        if (appState.selectedModule == BottomModule.Students && appState.selectedStudent == null) {
            FloatingActionIcon(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 108.dp),
                onClick = appState::openCreateStudentSheet,
            )
        }

        if (appState.showCreateStudentSheet) {
            StudentCreationSheet(
                onDismiss = appState::closeCreateStudentSheet,
                onCreateStudent = appState::createStudent,
            )
        }
    }
}
