package com.example.bloompal.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Plant(
    @DocumentId
    val plantId: String = "",                    // Firestore document ID
    val userId: String = "",                     // Owner's user ID
    val name: String = "",                       // Plant name
    val species: String = "",                    // Scientific name (optional)

    val plantType: PlantType = PlantType.FOLIAGE,
    val category: PlantCategory = PlantCategory.COMMON,
    val location: PlantLocation = PlantLocation.INDOOR,

    val isToxic: Boolean = false,
    val inBloom: Boolean = false,

    // 🌟 NEW — drawable resource image
    val imageRes: Int? = null,

    // Existing cloud URL image
    val imageUrl: String = "",

    val lastWatered: Timestamp? = null,
    val wateringFrequency: Int = 7,
    val nextWateringDate: Timestamp? = null,
    val lastPruned: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val description: String = ""
) {

    fun getDaysUntilWatering(): Int {
        val nextDate = nextWateringDate?.toDate() ?: return 0
        val today = Date()
        val diff = nextDate.time - today.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun needsWatering(): Boolean {
        return getDaysUntilWatering() <= 0
    }

    fun getDaysOverdue(): Int {
        val days = getDaysUntilWatering()
        return if (days < 0) -days else 0
    }

    fun calculateNextWateringDate(): Timestamp {
        val last = lastWatered?.toDate() ?: Date()
        val cal = java.util.Calendar.getInstance()
        cal.time = last
        cal.add(java.util.Calendar.DAY_OF_YEAR, wateringFrequency)
        return Timestamp(cal.time)
    }
}

enum class PlantType(val displayName: String) {
    FOLIAGE("Foliage Plant"),
    FLOWERING("Flowering Plant"),
    HERB("Herb"),
    SUCCULENT("Succulent");

    companion object {
        fun fromString(value: String): PlantType {
            return values().find { it.displayName == value } ?: FOLIAGE
        }
    }
}

enum class PlantCategory(val displayName: String) {
    EXOTIC("Exotic"),
    COMMON("Common");

    companion object {
        fun fromString(value: String): PlantCategory {
            return values().find { it.displayName == value } ?: COMMON
        }
    }
}

enum class PlantLocation(val displayName: String) {
    INDOOR("Indoor"),
    OUTDOOR("Outdoor");

    companion object {
        fun fromString(value: String): PlantLocation {
            return values().find { it.displayName == value } ?: INDOOR
        }
    }
}