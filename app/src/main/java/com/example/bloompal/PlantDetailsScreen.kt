@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bloompal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bloompal.data.model.Plant
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val PastelPink = Color(0xFFFFF5F7)
private val DarkGreen = Color(0xFF2D7A3E)

@Composable
fun PlantDetailsScreen(
    plant: Plant,
    onBack: () -> Unit,
    onEdit: (Plant) -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    val repo = PlantRepository()
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    // ⭐ FETCH WATERING HISTORY FROM FIRESTORE
    var wateringHistory by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoadingHistory by remember { mutableStateOf(true) }

    // ⭐ Check drawable validity
    val drawableIsValid = remember(plant.imageRes) {
        if (plant.imageRes == null) return@remember false
        try {
            context.resources.getResourceName(plant.imageRes)
            true
        } catch (e: Exception) {
            false
        }
    }

    LaunchedEffect(plant.plantId) {
        isLoadingHistory = true
        wateringHistory = repo.getWateringHistory(plant.plantId)
        isLoadingHistory = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(plant) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }

                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PastelPink
                )
            )
        },
        containerColor = PastelPink
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // IMAGE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        drawableIsValid -> {
                            Image(
                                painter = painterResource(id = plant.imageRes!!),
                                contentDescription = plant.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        plant.imageUrl.isNotBlank() -> {
                            AsyncImage(
                                model = plant.imageUrl,
                                contentDescription = plant.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🌿", fontSize = 80.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Details", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)

            Spacer(Modifier.height(12.dp))

            InfoRow("Name", plant.name)
            InfoRow("Species", plant.species.ifBlank { "Unknown" })
            InfoRow("Type", plant.plantType.displayName)
            InfoRow("Category", plant.category.displayName)
            InfoRow("Location", plant.location.displayName)
            InfoRow("Water every", "${plant.wateringFrequency} days")
            InfoRow("Toxic", if (plant.isToxic) "Yes" else "No")
            InfoRow("In Bloom", if (plant.inBloom) "Yes" else "No")

            Spacer(Modifier.height(16.dp))

            Text("Notes", fontWeight = FontWeight.Bold)
            Text(plant.description.ifBlank { "No notes added." })

            Spacer(Modifier.height(20.dp))

            // ⭐ WATERING HISTORY (FULLY FUNCTIONAL!)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Watering History", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (!isLoadingHistory) {
                            Text(
                                "${wateringHistory.size} ${if (wateringHistory.size == 1) "event" else "events"}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    when {
                        isLoadingHistory -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = DarkGreen
                                )
                            }
                        }
                        wateringHistory.isEmpty() -> {
                            Text(
                                "No watering history yet. Water this plant from the dashboard to start tracking! 💧",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        else -> {
                            // Show most recent watering events (sorted by timestamp, newest first)
                            val sortedHistory = wateringHistory.sortedByDescending { event ->
                                (event["timestamp"] as? Timestamp)?.toDate()?.time ?: 0L
                            }

                            sortedHistory.take(10).forEach { event ->
                                WateringHistoryItem(event)
                                Spacer(Modifier.height(8.dp))
                            }

                            if (wateringHistory.size > 10) {
                                Text(
                                    "Showing 10 most recent events (${wateringHistory.size} total)",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // DELETE CONFIRMATION DIALOG
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Plant?") },
            text = {
                Text("Are you sure you want to permanently delete this plant and all its watering history?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isDeleting) return@TextButton
                        isDeleting = true

                        scope.launch {
                            repo.deletePlant(plant.plantId)
                                .onSuccess {
                                    isDeleting = false
                                    showDeleteDialog = false
                                    onDeleteConfirmed()
                                }
                                .onFailure {
                                    isDeleting = false
                                    showDeleteDialog = false
                                }
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}

@Composable
fun WateringHistoryItem(event: Map<String, Any>) {
    val timestamp = event["timestamp"] as? Timestamp
    val note = event["note"] as? String ?: "Watered"

    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
    val dateString = timestamp?.toDate()?.let { dateFormat.format(it) } ?: "Unknown date"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50))
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                note,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D)
            )
            Text(
                dateString,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Text("💧", fontSize = 20.sp)
    }
}