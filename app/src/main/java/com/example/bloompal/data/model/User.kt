package com.example.bloompal.data.model
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * User data model
 * Stores basic user profile information
 *
 * Synced with Firebase Authentication and Firestore
 */
data class User(
    @DocumentId
    val userId: String = "",                    // Firebase Auth UID
    val name: String = "",                      // Display name
    val email: String = "",                     // Email address
    val createdAt: Timestamp = Timestamp.now()  // Account creation date
)