package com.example.bloompal

object SamplePlantImages {

    data class SampleImage(
        val name: String,
        val url: String,
        val thumbnail: String = url // Can use same URL or create thumbnails
    )

    val sampleImages = listOf(
        SampleImage(
            name = "Desert Rose",
            url = "https://firebasestorage.googleapis.com/v0/b/bloompal-4a4b0.firebasestorage.app/o/sample%20images%2Fdesert_rose.webp?alt=media&token=9aca1895-9cbd-448f-ae3d-7bf169aa5a7d"),
        SampleImage(
            name = "Lavender",
            url = "https://firebasestorage.googleapis.com/v0/b/bloompal-4a4b0.firebasestorage.app/o/sample%20images%2Flavender.jpg?alt=media&token=a93441ce-5cf4-4396-be13-161d41d7f73a"),
        SampleImage(
            name = "Philodendron",
            url = "https://firebasestorage.googleapis.com/v0/b/bloompal-4a4b0.firebasestorage.app/o/sample%20images%2Fphilodendron.jpg?alt=media&token=a80f446e-2e4f-432a-8742-c7be3b62009d"),
        SampleImage(
            name = "Prayer Plant",
            url = "https://firebasestorage.googleapis.com/v0/b/bloompal-4a4b0.firebasestorage.app/o/sample%20images%2Fprayer_plant.jpg?alt=media&token=d81a6d48-6755-422c-a1ee-13b9cf78d26f"),
        SampleImage(
            name = "Schefflera",
            url = "https://firebasestorage.googleapis.com/v0/b/bloompal-4a4b0.firebasestorage.app/o/sample%20images%2Fschefflera.jpg?alt=media&token=1590d500-70ad-4069-8e34-5dcfe2038b67")
    )
}