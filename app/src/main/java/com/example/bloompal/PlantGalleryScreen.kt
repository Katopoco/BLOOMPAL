@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bloompal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bloompal.data.model.Plant
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlantListScreen(
    onAddPlant: () -> Unit,
    onPlantSelected: (Plant) -> Unit
) {
    val repo = PlantRepository()
    var plants by remember { mutableStateOf<List<Plant>>(emptyList()) }

    // Real-time listener
    LaunchedEffect(true) {
        repo.getUserPlants().collectLatest { list ->
            plants = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Plants", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF5F7)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlant,
                containerColor = Color(0xFF2D7A3E),
                contentColor = Color.White
            ) {
                Text("+")
            }
        }
    ) { padding ->

        if (plants.isEmpty()) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No plants yet. Add one 🌿", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(padding)
                    .padding(10.dp)
            ) {
                items(plants) { plant ->
                    PlantCard(plant) { onPlantSelected(plant) }
                }
            }
        }
    }
}

@Composable
fun PlantCard(plant: Plant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            AsyncImage(
                model = plant.imageUrl.ifBlank { plant.imageRes },
                contentDescription = plant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text(plant.name, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                plant.species,
                color = Color.Gray,
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}