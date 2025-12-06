package com.example.bloompal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF4CAF50),
                    secondary = Color(0xFF8BC34A)
                )
            ) {

                var currentScreen by remember { mutableStateOf("login") }
                var selectedPlantId by remember { mutableStateOf<String?>(null) }

                val authViewModel: AuthViewModel = viewModel()
                val dashboardViewModel: DashboardViewModel = viewModel()
                val profileViewModel: ProfileViewModel = viewModel()

                val authState by authViewModel.authState.collectAsState()
                val dashboardUiState by dashboardViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    authViewModel.setOnUserChangedListener { isLoggedIn ->
                        if (isLoggedIn) {
                            dashboardViewModel.onUserLoggedIn()
                        } else {
                            dashboardViewModel.onUserLoggedOut()
                        }
                    }
                }

                LaunchedEffect(authState.isAuthenticated) {
                    if (authState.isAuthenticated) {
                        profileViewModel.refreshUserData()

                        if (currentScreen == "login" || currentScreen == "signup") {
                            currentScreen = "dashboard"
                        }
                    } else {
                        currentScreen = "login"
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    when (currentScreen) {

                        "login" -> LoginScreen(
                            authViewModel = authViewModel,
                            onNavigateToSignUp = { currentScreen = "signup" },
                            onLoginSuccess = {
                                profileViewModel.refreshUserData()
                                currentScreen = "dashboard"
                            }
                        )

                        "signup" -> SignUpScreen(
                            authViewModel = authViewModel,
                            onNavigateToLogin = { currentScreen = "login" },
                            onSignUpSuccess = {
                                profileViewModel.refreshUserData()
                                currentScreen = "dashboard"
                            }
                        )

                        "dashboard" -> DashboardScreen(
                            viewModel = dashboardViewModel,
                            profileViewModel = profileViewModel,
                            onNavigateToCollection = { currentScreen = "collection" },
                            onNavigateToAddPlant = { currentScreen = "addPlant" },
                            onNavigateToSettings = { currentScreen = "settings" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )

                        "collection" -> PlantCollectionScreen(
                            viewModel = dashboardViewModel,
                            onBackClick = { currentScreen = "dashboard" },
                            onPlantClick = { plant ->
                                selectedPlantId = plant.plantId
                                currentScreen = "details"
                            }
                        )

                        "details" -> {
                            val plant = dashboardUiState.plants
                                .firstOrNull { it.plantId == selectedPlantId }

                            if (plant != null) {
                                PlantDetailsScreen(
                                    plant = plant,
                                    onBack = {
                                        selectedPlantId = null
                                        currentScreen = "collection"
                                    },
                                    onEdit = { p ->
                                        selectedPlantId = p.plantId
                                        currentScreen = "editPlant"
                                    },
                                    onDeleteConfirmed = {
                                        selectedPlantId = null
                                        currentScreen = "collection"
                                    }
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                                LaunchedEffect(Unit) {
                                    selectedPlantId = null
                                    currentScreen = "collection"
                                }
                            }
                        }

                        "editPlant" -> {
                            val plant = dashboardUiState.plants
                                .firstOrNull { it.plantId == selectedPlantId }

                            if (plant != null) {
                                EditPlantScreen(
                                    plant = plant,
                                    onBack = { currentScreen = "details" },
                                    onSaved = {
                                        currentScreen = "details"
                                    }
                                )
                            }
                        }

                        "addPlant" -> AddPlantScreen(
                            onBack = { currentScreen = "dashboard" },
                            onSaved = {
                                currentScreen = "dashboard"
                                Toast.makeText(
                                    this@MainActivity,
                                    "Plant added successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                        "settings" -> SettingsScreen(
                            authViewModel = authViewModel,
                            profileViewModel = profileViewModel,
                            onBackClick = { currentScreen = "dashboard" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )

                        "profile" -> ProfileScreen(
                            dashboardViewModel = dashboardViewModel,
                            profileViewModel = profileViewModel,
                            authViewModel = authViewModel,
                            onBackClick = { currentScreen = "dashboard" },
                            onNavigateToSettings = { currentScreen = "settings" },
                            onNavigateToEditProfile = { currentScreen = "editProfile" },
                            onNavigateToWateringHistory = { currentScreen = "wateringHistory" }
                        )

                        "editProfile" -> EditProfileScreen(
                            profileViewModel = profileViewModel,
                            onBackClick = { currentScreen = "profile" },
                            onSaveProfile = { name, bio, email, phone ->
                                profileViewModel.updateProfile(name, bio, email, phone)
                                Toast.makeText(
                                    this@MainActivity,
                                    "Profile updated!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                        "wateringHistory" -> WateringHistoryScreen(
                            dashboardViewModel = dashboardViewModel,
                            onBackClick = { currentScreen = "profile" }
                        )
                    }
                }
            }
        }
    }
}