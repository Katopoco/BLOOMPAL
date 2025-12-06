@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bloompal

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bloompal.data.model.*
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp

private val PastelPink = Color(0xFFFFF5F7)
private val DarkGreen = Color(0xFF2D7A3E)

@Composable
fun AddPlantScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val repo = PlantRepository()
    val storageRepo = StorageRepository()
    val scope = rememberCoroutineScope()

    // Form state
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var plantType by remember { mutableStateOf(PlantType.FOLIAGE) }
    var category by remember { mutableStateOf(PlantCategory.COMMON) }
    var location by remember { mutableStateOf(PlantLocation.INDOOR) }
    var wateringFrequency by remember { mutableStateOf("7") }
    var description by remember { mutableStateOf("") }
    var isToxic by remember { mutableStateOf(false) }
    var inBloom by remember { mutableStateOf(false) }
    var pickedImage: Uri? by remember { mutableStateOf(null) }
    var selectedSampleImageUrl by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var showSampleImagePicker by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        pickedImage = uri
        selectedSampleImageUrl = null // Clear sample image if user picks from device
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Plant", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            // Image Picker
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                        when {
                            selectedSampleImageUrl != null -> {
                                AsyncImage(
                                    model = selectedSampleImageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            pickedImage != null -> {
                                AsyncImage(
                                    model = pickedImage,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Text("Tap to add photo", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ⭐ SAMPLE IMAGES BUTTON FOR EXAM DEMO
                    OutlinedButton(
                        onClick = { showSampleImagePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📸 Use Sample Image")
                    }

                    if (errorMessage != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // BASIC INFO
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Color.White),
                modifier = Modifier.fillMaxWidth()
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
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Color.White),
                modifier = Modifier.fillMaxWidth()
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

            // CARE
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text("Care Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = wateringFrequency,
                        onValueChange = { if (it.all(Char::isDigit)) wateringFrequency = it },
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(isToxic, { isToxic = it })
                            Text("Toxic")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(inBloom, { inBloom = it })
                            Text("Currently in bloom")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter a plant name."
                        return@Button
                    }

                    scope.launch {
                        isSaving = true
                        errorMessage = null

                        var finalUrl = ""

                        // ⭐ USE SAMPLE IMAGE URL IF SELECTED, OTHERWISE UPLOAD FROM DEVICE
                        if (selectedSampleImageUrl != null) {
                            finalUrl = selectedSampleImageUrl!!
                        } else if (pickedImage != null) {
                            storageRepo.uploadPlantImage(pickedImage!!)
                                .onSuccess { finalUrl = it }
                                .onFailure { e ->
                                    errorMessage = e.message
                                    isSaving = false
                                    return@launch
                                }
                        }

                        val plant = Plant(
                            name = name,
                            species = species.ifBlank { "" },
                            plantType = plantType,
                            category = category,
                            location = location,
                            wateringFrequency = wateringFrequency.toIntOrNull() ?: 7,
                            description = description,
                            isToxic = isToxic,
                            inBloom = inBloom,
                            imageUrl = finalUrl,
                            lastWatered = Timestamp.now(),
                            nextWateringDate = Timestamp.now(),
                            imageRes = null
                        )

                        val result = repo.addPlant(plant)
                        result.onSuccess {
                            isSaving = false
                            onSaved()
                        }.onFailure { e ->
                            errorMessage = "Failed to save: ${e.message}"
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                enabled = !isSaving
            ) {
                Text(if (isSaving) "Saving…" else "Save Plant", fontWeight = FontWeight.Bold)
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