package com.example.bloompal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bloompal.data.model.Plant

// 🎨 Pastel Brand Colors
private val PastelPink = Color(0xFFFFF5F7)
private val PastelGreen = Color(0xFFD4EDDA)
private val DarkGreen = Color(0xFF2D7A3E)
private val CardPink = Color(0xFFFFE5E5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    onNavigateToCollection: () -> Unit,
    onNavigateToAddPlant: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val profileData by profileViewModel.profileData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BloomPal", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelPink)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = PastelPink) {

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFF4CAF50)) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCollection,
                    icon = { Text("🪴", fontSize = 28.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToAddPlant,
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        }
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
        },
        containerColor = PastelPink
    ) { padding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DarkGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    Spacer(Modifier.height(24.dp))

                    // Greeting
                    Column {
                        Text("Welcome back,", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text(
                            profileData.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B6B6B)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Empty State
                    if (uiState.plants.isEmpty()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No plants yet — import demo plants or add your own!",
                                textAlign = TextAlign.Center
                            )
                            Button(onClick = { viewModel.importDemoData() }) {
                                Text("Import Demo Data")
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // 🌿 Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatCard("Total Plants", uiState.totalPlants.toString(), "🌿")
                        StatCard("Need Water", uiState.needWater.toString(), "💧")
                        StatCard("In Bloom", uiState.inBloom.toString(), "🌸")
                    }

                    Spacer(Modifier.height(24.dp))

                    TodayCareHeader(onSeeAllClick = onNavigateToCollection)

                    Spacer(Modifier.height(16.dp))
                }

                // Care list
                items(uiState.plantsNeedingCare.size) { index ->
                    val plant = uiState.plantsNeedingCare[index]
                    PlantCareCard(
                        plant = plant,
                        onMarkAsWatered = {
                            viewModel.markAsWatered(plant.plantId)
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}


@Composable
fun StatCard(label: String, value: String, emoji: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(110.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 24.sp)
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 11.sp, color = Color(0xFF555555))
        }
    }
}


@Composable
fun TodayCareHeader(onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Today's Care", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        TextButton(onClick = onSeeAllClick) {
            Text("See all", color = DarkGreen, fontWeight = FontWeight.SemiBold)
        }
    }
}


@Composable
fun PlantCareCard(plant: Plant, onMarkAsWatered: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardPink),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ⭐ FIXED: Safe image loading - catches missing resources
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    plant.imageUrl.isNotBlank() -> {
                        // Try URL first
                        AsyncImage(
                            model = plant.imageUrl,
                            contentDescription = plant.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    plant.imageRes != null -> {
                        val context = LocalContext.current
                        val canLoadDrawable = try {
                            context.resources.getResourceName(plant.imageRes!!)
                            true
                        } catch (e: Exception) {
                            false
                        }

                        if (canLoadDrawable) {
                            Image(
                                painter = painterResource(id = plant.imageRes!!),
                                contentDescription = plant.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("🌿", fontSize = 32.sp)
                        }
                    }
                    else -> {
                        // No image available
                        Text("🌿", fontSize = 32.sp)
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(plant.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${plant.plantType.displayName} • ${plant.category.displayName}",
                    fontSize = 13.sp, color = Color(0xFF6B6B6B))
                Text(plant.location.displayName, fontSize = 12.sp, color = Color(0xFF7A7A7A))
            }

            Button(
                onClick = onMarkAsWatered,
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = Color.White)
            ) { Text("Water") }
        }
    }
}