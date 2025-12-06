package com.example.bloompal

import com.example.bloompal.data.model.*
import com.google.firebase.Timestamp

object MockDataSeeder {

    val demoPlants: List<Plant> = listOf(

        Plant(
            plantId = "",
            userId = "",
            name = "Snake Plant",
            species = "Sansevieria trifasciata",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = true,
            inBloom = false,
            wateringFrequency = 14,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.snake_plant,
            description = "Hardy, drought-tolerant plant. Thrives on neglect!"
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Monstera",
            species = "Monstera deliciosa",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = true,
            inBloom = false,
            wateringFrequency = 7,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.monstera,
            description = "Iconic split leaves. Loves bright indirect light."
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Peace Lily",
            species = "Spathiphyllum",
            plantType = PlantType.FLOWERING,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = true,
            inBloom = true,
            wateringFrequency = 5,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.peace_lily,
            description = "Air purifying plant with elegant white blooms."
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "ZZ Plant",
            species = "Zamioculcas zamiifolia",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = true,
            inBloom = false,
            wateringFrequency = 14,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.zz_plant,
            description = "Nearly indestructible. Perfect for beginners!"
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Chinese Money Plant",
            species = "Pilea peperomioides",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = false,
            inBloom = false,
            wateringFrequency = 7,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.chinese_money_plant,
            description = "Cute coin-shaped leaves. Easy to propagate!"
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Orchid",
            species = "Phalaenopsis",
            plantType = PlantType.FLOWERING,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = false,
            inBloom = true,
            wateringFrequency = 7,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.orchid,
            description = "Elegant flowering plant. Needs humidity and indirect light."
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "String of Pearls",
            species = "Senecio rowleyanus",
            plantType = PlantType.SUCCULENT,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = true,
            inBloom = false,
            wateringFrequency = 10,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.string_of_pearls,
            description = "Unique trailing succulent with bead-like leaves."
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Aglaonema",
            species = "Aglaonema",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = true,
            inBloom = false,
            wateringFrequency = 7,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.aglaonema,
            description = "Colorful foliage plant. Great for low light areas."
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Caladium",
            species = "Caladium bicolor",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = true,
            inBloom = false,
            wateringFrequency = 3,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.caladium,
            description = "Stunning heart-shaped leaves with vibrant patterns."
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Croton",
            species = "Codiaeum variegatum",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = true,
            inBloom = false,
            wateringFrequency = 5,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.croton,
            description = "Bold, colorful leaves. Needs bright light to maintain colors."
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Syngonium",
            species = "Syngonium podophyllum",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = true,
            inBloom = false,
            wateringFrequency = 5,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.syngonium,
            description = "Arrow-shaped leaves. Fast growing and easy to care for."
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Tradescantia",
            species = "Tradescantia zebrina",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.INDOOR,
            isToxic = false,
            inBloom = false,
            wateringFrequency = 5,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.tradescantia,
            description = "Beautiful striped purple leaves. Great for hanging baskets."
        ),

        Plant(
            plantId = "",
            userId = "",
            name = "Yucca",
            species = "Yucca elephantipes",
            plantType = PlantType.FOLIAGE,
            category = PlantCategory.COMMON,
            location = PlantLocation.OUTDOOR,
            isToxic = true,
            inBloom = false,
            wateringFrequency = 14,
            lastWatered = Timestamp.now(),
            nextWateringDate = Timestamp.now(),
            imageUrl = "",
            imageRes = R.drawable.yucca,
            description = "Architectural plant with sword-like leaves. Very drought tolerant."
        )
    )
}