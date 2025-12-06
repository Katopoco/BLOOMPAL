package com.example.bloompal

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileData(
    val name: String = "User",
    val bio: String = "Plant Enthusiast 🌿",
    val email: String = "",
    val phone: String = "+233 24 123 4567",
    val location: String = "Accra, Ghana",
    val wateringSchedule: String = "Morning & Evening",
    val measurementUnit: String = "Metric (cm, ml)",
    val language: String = "English"
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _profileData = MutableStateFlow(ProfileData())
    val profileData: StateFlow<ProfileData> = _profileData.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        Log.d("ProfileViewModel", "=== LOADING USER DATA ===")
        Log.d("ProfileViewModel", "Current user: $currentUser")

        if (currentUser != null) {
            Log.d("ProfileViewModel", "User email: ${currentUser.email}")
            Log.d("ProfileViewModel", "User displayName: ${currentUser.displayName}")

            // FIX: Check for empty string too!
            val extractedName = currentUser.displayName?.takeIf { it.isNotEmpty() }
                ?: currentUser.email?.substringBefore("@")
                ?: "User"

            Log.d("ProfileViewModel", "Extracted name: '$extractedName'")

            _profileData.value = _profileData.value.copy(
                name = extractedName,
                email = currentUser.email ?: ""
            )

            Log.d("ProfileViewModel", "Final profileData: ${_profileData.value}")
        } else {
            Log.d("ProfileViewModel", "NO CURRENT USER FOUND!")
        }
    }

    fun updateProfile(name: String, bio: String, email: String, phone: String) {
        _profileData.value = _profileData.value.copy(
            name = name,
            bio = bio,
            email = email,
            phone = phone
        )
        Log.d("ProfileViewModel", "Profile updated: ${_profileData.value}")
    }

    fun updateWateringSchedule(schedule: String) {
        _profileData.value = _profileData.value.copy(wateringSchedule = schedule)
    }

    fun updateMeasurementUnit(unit: String) {
        _profileData.value = _profileData.value.copy(measurementUnit = unit)
    }

    fun updateLanguage(language: String) {
        _profileData.value = _profileData.value.copy(language = language)
    }

    fun refreshUserData() {
        loadUserData()
    }
}