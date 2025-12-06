@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bloompal

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bloompal.data.model.Plant
import com.example.bloompal.data.model.PlantCategory
import com.example.bloompal.data.model.PlantLocation
import com.example.bloompal.data.model.PlantType
import kotlinx.coroutines.launch

private val PastelPink = Color(0xFFFFF5F7)
private val DarkGreen = Color(0xFF2D7A3E)

@Composable
fun EditPlantScreen(
    plant: Plant,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val repo = PlantRepository()
    val storageRepo = StorageRepository()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(plant.name) }
    var species by remember { mutableStateOf(plant.species) }
    var plantType by remember { mutableStateOf(plant.plantType) }
    var category by remember { mutableStateOf(plant.category) }
    var location by remember { mutableStateOf(plant.location) }
    var wateringFrequency by remember { mutableStateOf(plant.wateringFrequency.toString()) }
    var description by remember { mutableStateOf(plant.description) }
    var isToxic by remember { mutableStateOf(plant.isToxic) }
    var inBloom by remember { mutableStateOf(plant.inBloom) }

    var pickedImage: Uri? by remember { mutableStateOf(null) }
    var selectedSampleImageUrl by remember { mutableStateOf<String?>(null) }
    var imageUrl by remember { mutableStateOf(plant.imageUrl) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var showSampleImagePicker by remember { mutableStateOf(false) }

    // Image picker
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { }

            pickedImage = uri
            selectedSampleImageUrl = null // Clear sample image if user picks from device
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Plant", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelPink)
            )
        },
        containerColor = PastelPink
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // IMAGE PREVIEW + PICKER
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF2F2F2))
                            .border(3.dp, DarkGreen.copy(alpha = 0.4f), CircleShape)
                            .clickable { imagePicker.launch(arrayOf("image/*")) },
                        contentAlignment = Alignment.Center
                    ) {
                        // ⭐ PRIORITY: Sample image > Picked image > Existing URL
                        val displayImage = when {
                            selectedSampleImageUrl != null -> selectedSampleImageUrl
                            pickedImage != null -> pickedImage
                            else -> imageUrl
                        }

                        if (displayImage != null && displayImage.toString().isNotBlank()) {
                            AsyncImage(
                                model = displayImage,
                                contentDescription = "Plant image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Tap to add photo", color = Color.Gray, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ⭐ SAMPLE IMAGES BUTTON
                    OutlinedButton(
                        onClick = { showSampleImagePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📸 Use Sample Image")
                    }

                    errorMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(text = it, color = Color.Red, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // BASIC INFO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Basic Info", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Plant name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = species,
                        onValueChange = { species = it },
                        label = { Text("Species (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // CLASSIFICATION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text("Classification", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))

                    PlantDropdownField(
                        label = "Plant Type",
                        value = plantType.displayName,
                        options = PlantType.entries.map { it.displayName },
                        onOptionSelected = { plantType = PlantType.fromString(it) }
                    )

                    Spacer(Modifier.height(12.dp))

                    PlantDropdownField(
                        label = "Category",
                        value = category.displayName,
                        options = PlantCategory.entries.map { it.displayName },
                        onOptionSelected = { category = PlantCategory.fromString(it) }
                    )

                    Spacer(Modifier.height(12.dp))

                    PlantDropdownField(
                        label = "Location",
                        value = location.displayName,
                        options = PlantLocation.entries.map { it.displayName },
                        onOptionSelected = { location = PlantLocation.fromString(it) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // CARE DETAILS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text("Care Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = wateringFrequency,
                        onValueChange = { text ->
                            if (text.all { it.isDigit() } || text.isEmpty()) {
                                wateringFrequency = text
                            }
                        },
                        label = { Text("Watering every (days)") },
                        leadingIcon = { Text("💧") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Notes / description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isToxic, onCheckedChange = { isToxic = it })
                            Text("Toxic", fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = inBloom, onCheckedChange = { inBloom = it })
                            Text("Currently in bloom", fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // SAVE BUTTON
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter at least a plant name."
                        return@Button
                    }

                    scope.launch {
                        isSaving = true
                        errorMessage = null

                        var finalImageUrl = imageUrl

                        // ⭐ PRIORITY: Sample image URL > Upload new picked image > Keep existing
                        if (selectedSampleImageUrl != null) {
                            finalImageUrl = selectedSampleImageUrl!!
                        } else if (pickedImage != null) {
                            storageRepo.uploadPlantImage(pickedImage!!)
                                .onSuccess { finalImageUrl = it }
                                .onFailure { e ->
                                    errorMessage = "Image upload failed: ${e.message}"
                                    isSaving = false
                                    return@launch
                                }
                        }

                        val updates = mapOf(
                            "name" to name,
                            "species" to species,
                            "description" to description,
                            "plantType" to plantType,
                            "category" to category,
                            "location" to location,
                            "wateringFrequency" to (wateringFrequency.toIntOrNull()
                                ?: plant.wateringFrequency),
                            "isToxic" to isToxic,
                            "inBloom" to inBloom,
                            "imageUrl" to finalImageUrl
                        )

                        repo.updatePlant(plant.plantId, updates)
                            .onSuccess {
                                isSaving = false
                                onSaved()
                            }
                            .onFailure { e ->
                                errorMessage = "Failed to save changes: ${e.message}"
                                isSaving = false
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text(if (isSaving) "Saving..." else "Save Changes", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Cancel", color = Color.Gray)
            }
        }
    }

    // ⭐ SAMPLE IMAGE PICKER DIALOG
    if (showSampleImagePicker) {
        SampleImagePickerDialog(
            onDismiss = { showSampleImagePicker = false },
            onImageSelected = { url ->
                selectedSampleImageUrl = url
                pickedImage = null // Clear device image if sample selected
            }
        )
    }
}