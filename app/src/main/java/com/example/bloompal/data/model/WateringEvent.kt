package com.example.bloompal.data.model

import com.google.firebase.Timestamp

data class WateringEvent(
    val timestamp: Timestamp = Timestamp.now(),
    val note: String = "Watered! and Fresh!" // Optional note like "Watered via dashboard" or "Manual watering"
) {
    // Firestore needs no-arg constructor
    constructor() : this(Timestamp.now(), "")
}