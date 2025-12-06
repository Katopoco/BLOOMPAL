package com.example.bloompal

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Upload plant image and return download URL
    suspend fun uploadPlantImage(uri: Uri): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in."))

            val fileName = "plant_${System.currentTimeMillis()}.jpg"
            val ref = storage.reference
                .child("users")
                .child(userId)
                .child("plantImages")
                .child(fileName)

            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()

            Result.success(downloadUrl)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}