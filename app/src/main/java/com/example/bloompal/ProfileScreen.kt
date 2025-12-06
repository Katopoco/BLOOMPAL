@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bloompal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val PastelPink = Color(0xFFFFF5F7)
private val DarkGreen = Color(0xFF2D7A3E)
private val LightGreen = Color(0xFFE8F5E9)
private val PastelYellow = Color(0xFFFFF3CD)

@Composable
fun ProfileScreen(
    dashboardViewModel: DashboardViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onBackClick: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToWateringHistory: () -> Unit
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val profileData by profileViewModel.profileData.collectAsState()

    var showWateringDialog by remember { mutableStateOf(false) }
    var showUnitsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = DarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelPink)
            )
        },
        containerColor = PastelPink
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            Spacer(Modifier.height(24.dp))

            // ---------- Profile Picture ----------
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(DarkGreen)
                        .border(4.dp, Color.White, CircleShape)
                        .clickable { onNavigateToEditProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                }

                Spacer(Modifier.height(16.dp))

                Text(profileData.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(profileData.bio, fontSize = 15.sp, color = Color(0xFF6B6B6B))

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onNavigateToEditProfile,
                    modifier = Modifier.height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkGreen)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = DarkGreen)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Profile", color = DarkGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(32.dp))

            // ---------- Stats ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(uiState.totalPlants.toString(), "Plants", "🌱", LightGreen, Modifier.weight(1f))
                ProfileStatCard(uiState.needWater.toString(), "Need Care", "💧", PastelYellow, Modifier.weight(1f))
                ProfileStatCard(uiState.inBloom.toString(), "In Bloom", "🌸", Color(0xFFFFC9C9), Modifier.weight(1f))
            }

            Spacer(Modifier.height(32.dp))

            // ---------- Account ----------
            Text("Account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    ProfileMenuItem(Icons.Default.Email, "Email", profileData.email) { onNavigateToEditProfile() }

                    Divider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))

                    ProfileMenuItem(Icons.Default.Phone, "Phone", profileData.phone) { onNavigateToEditProfile() }

                    Divider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))

                    ProfileMenuItem(Icons.Default.LocationOn, "Location", profileData.location) { onNavigateToEditProfile() }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ---------- Preferences ----------
            Text("Preferences", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    ProfileMenuItemEmoji("🌙", "Watering Schedule", profileData.wateringSchedule) {
                        showWateringDialog = true
                    }

                    Divider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))

                    ProfileMenuItemEmoji("📏", "Measurement Units", profileData.measurementUnit) {
                        showUnitsDialog = true
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ---------- Activity ----------
            Text("My Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    ProfileMenuItemEmoji("📊", "Watering History", "View your care logs") {
                        onNavigateToWateringHistory()
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ---------- Logout ----------
            OutlinedButton(
                onClick = { authViewModel.signOut() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD32F2F))
                )
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFD32F2F))
                Spacer(Modifier.width(8.dp))
                Text("Logout", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // ---------- Dialogs ----------
    if (showWateringDialog) {
        PreferenceDialog(
            title = "Watering Schedule",
            options = listOf("Morning", "Evening", "Morning & Evening", "Afternoon"),
            currentValue = profileData.wateringSchedule,
            onDismiss = { showWateringDialog = false },
            onSelect = {
                profileViewModel.updateWateringSchedule(it)
                showWateringDialog = false
            }
        )
    }

    if (showUnitsDialog) {
        PreferenceDialog(
            title = "Measurement Units",
            options = listOf("Metric (cm, ml)", "Imperial (in, fl oz)"),
            currentValue = profileData.measurementUnit,
            onDismiss = { showUnitsDialog = false },
            onSelect = {
                profileViewModel.updateMeasurementUnit(it)
                showUnitsDialog = false
            }
        )
    }
}

@Composable
fun ProfileStatCard(number: String, label: String, icon: String, backgroundColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(number, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(45.dp).clip(CircleShape).background(LightGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = DarkGreen)
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 14.sp, color = Color.Gray)
        }

        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun ProfileMenuItemEmoji(emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(45.dp).clip(CircleShape).background(LightGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 22.sp)
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 14.sp, color = Color.Gray)
        }

        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun PreferenceDialog(
    title: String,
    options: List<String>,
    currentValue: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == currentValue,
                            onClick = { onSelect(option) }
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(option, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = Color.White
    )
}