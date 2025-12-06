package com.example.bloompal

import com.example.bloompal.data.model.Plant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PlantRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun userPlantsCollection() =
        firestore.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("plants")

    // ----------------------------------------------------------------------
    // REAL-TIME LISTENER
    // ----------------------------------------------------------------------
    fun getUserPlants(): Flow<List<Plant>> = callbackFlow {
        val listener: ListenerRegistration = userPlantsCollection()
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val plants = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Plant::class.java)?.copy(plantId = doc.id)
                } ?: emptyList()

                trySend(plants).isSuccess
            }

        awaitClose { listener.remove() }
    }

    // ----------------------------------------------------------------------
    // ONE-TIME FETCH
    // ----------------------------------------------------------------------
    suspend fun getPlantsOnce(): List<Plant> {
        val snapshot = userPlantsCollection().get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Plant::class.java)?.copy(plantId = doc.id)
        }
    }

    // ⭐ NEW: GET WATERING HISTORY FOR A SPECIFIC PLANT
    suspend fun getWateringHistory(plantId: String): List<Map<String, Any>> {
        return try {
            val doc = userPlantsCollection().document(plantId).get().await()
            @Suppress("UNCHECKED_CAST")
            (doc.get("wateringHistory") as? List<Map<String, Any>>) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ----------------------------------------------------------------------
    // ADD PLANT
    // ----------------------------------------------------------------------
    suspend fun addPlant(plant: Plant): Result<Unit> {
        return try {
            val doc = userPlantsCollection().document()
            val updated = plant.copy(
                plantId = doc.id,
                userId = auth.currentUser?.uid ?: ""
            )

            doc.set(updated).await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------------------------
    // UPDATE PLANT
    // ----------------------------------------------------------------------
    suspend fun updatePlant(id: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            userPlantsCollection().document(id).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------------------------
    // DELETE PLANT
    // ----------------------------------------------------------------------
    suspend fun deletePlant(id: String): Result<Unit> {
        return try {
            userPlantsCollection().document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}