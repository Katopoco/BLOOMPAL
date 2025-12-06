@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bloompal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val PastelPink = Color(0xFFFFF5F7)
private val DarkGreen = Color(0xFF2D7A3E)

data class WateringEventWithPlant(
    val plantName: String,
    val plantImageUrl: String,
    val plantImageRes: Int?,
    val timestamp: Timestamp,
    val note: String
)

@Composable
fun WateringHistoryScreen(
    dashboardViewModel: DashboardViewModel,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var allWateringEvents by remember { mutableStateOf<List<WateringEventWithPlant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // ⭐ FETCH ALL WATERING HISTORY FROM ALL PLANTS
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val repo = PlantRepository()
            val events = mutableListOf<WateringEventWithPlant>()

            // Get all plants
            val plants = repo.getPlantsOnce()

            // For each plant, get its watering history
            plants.forEach { plant ->
                val history = repo.getWateringHistory(plant.plantId)

                // Convert to WateringEventWithPlant
                history.forEach { event ->
                    val timestamp = event["timestamp"] as? Timestamp
                    val note = event["note"] as? String ?: "Watered"

                    if (timestamp != null) {
                        events.add(
                            WateringEventWithPlant(
                                plantName = plant.name,
                                plantImageUrl = plant.imageUrl,
                                plantImageRes = plant.imageRes,
                                timestamp = timestamp,
                                note = note
                            )
                        )
                    }
                }
            }

            // Sort by timestamp (most recent first)
            allWateringEvents = events.sortedByDescending { it.timestamp.toDate().time }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Watering History", fontWeight = FontWeight.Bold)
                        Text(
                            "All your plant care logs",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelPink)
            )
        },
        containerColor = PastelPink
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkGreen)
                }
            }

            allWateringEvents.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💧", fontSize = 60.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No watering history yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Water your plants from the dashboard to start tracking!",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Header with count
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(DarkGreen)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Total Care Events",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        "${allWateringEvents.size}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text("💧", fontSize = 48.sp)
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }

                    // Group by date
                    val groupedByDate = allWateringEvents.groupBy { event ->
                        val calendar = Calendar.getInstance()
                        calendar.time = event.timestamp.toDate()
                        "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
                    }

                    groupedByDate.forEach { (dateKey, events) ->
                        // Date header
                        item {
                            val firstEvent = events.first()
                            val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
                            val dateString = dateFormat.format(firstEvent.timestamp.toDate())

                            Text(
                                dateString,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Events for this date
                        items(events) { event ->
                            WateringEventCard(event)
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    item {
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun WateringEventCard(event: WateringEventWithPlant) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val timeString = timeFormat.format(event.timestamp.toDate())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plant image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    event.plantImageRes != null -> {
                        Image(
                            painter = painterResource(id = event.plantImageRes),
                            contentDescription = event.plantName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    event.plantImageUrl.isNotBlank() -> {
                        AsyncImage(
                            model = event.plantImageUrl,
                            contentDescription = event.plantName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Text("🌿", fontSize = 28.sp)
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            // Plant info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.plantName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
                Text(
                    event.note,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    timeString,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Water droplet icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("💧", fontSize = 20.sp)
            }
        }
    }
}