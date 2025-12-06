package com.example.bloompal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloompal.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import java.util.Calendar

data class DashboardUiState(
    val plants: List<Plant> = emptyList(),
    val totalPlants: Int = 0,
    val needWater: Int = 0,
    val inBloom: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val plantsNeedingCare: List<Plant> = emptyList()
)

class DashboardViewModel : ViewModel() {

    private val repository = PlantRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        if (auth.currentUser != null) {
            listenToPlants()
        }
    }

    private fun listenToPlants() {
        viewModelScope.launch {
            repository.getUserPlants()
                .onEach { plants ->
                    updateUi(plants)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect()
        }
    }

    fun onUserLoggedIn() {
        _uiState.value = DashboardUiState(isLoading = true)
        listenToPlants()
    }

    fun onUserLoggedOut() {
        _uiState.value = DashboardUiState()
    }

    private fun updateUi(plants: List<Plant>) {
        val needingWater = plants.filter { it.needsWatering() }

        _uiState.value = DashboardUiState(
            plants = plants,
            totalPlants = plants.size,
            needWater = needingWater.size,
            inBloom = plants.count { it.inBloom },
            plantsNeedingCare = needingWater,
            isLoading = false
        )
    }

    fun refreshPlants() {
        viewModelScope.launch {
            try {
                val plants = repository.getPlantsOnce()
                updateUi(plants)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    // ⭐ UPDATED: Add watering event to history
    fun markAsWatered(plantId: String) {
        viewModelScope.launch {
            val plant = _uiState.value.plants.firstOrNull { it.plantId == plantId }

            if (plant != null) {
                val now = Timestamp.now()

                // Calculate next watering date
                val calendar = Calendar.getInstance()
                calendar.time = now.toDate()
                calendar.add(Calendar.DAY_OF_YEAR, plant.wateringFrequency)
                val nextWateringDate = Timestamp(calendar.time)

                // Create watering event
                val wateringEvent = mapOf(
                    "timestamp" to now,
                    "note" to "Watered from dashboard"
                )

                // Update plant with new timestamps AND add to watering history
                repository.updatePlant(
                    plantId,
                    mapOf(
                        "lastWatered" to now,
                        "nextWateringDate" to nextWateringDate,
                        "wateringHistory" to FieldValue.arrayUnion(wateringEvent)
                    )
                )
            }
        }
    }

    fun deletePlant(plantId: String) {
        viewModelScope.launch {
            repository.deletePlant(plantId)
        }
    }

    fun importDemoData() {
        viewModelScope.launch {
            MockDataSeeder.demoPlants.forEach { plant ->
                repository.addPlant(plant)
            }
        }
    }
}