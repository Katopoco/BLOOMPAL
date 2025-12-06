// Top-level build file
plugins {
    id("com.android.application") version "8.3.0" apply false  // ← UPDATED!
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)  // ← Also fixed this
}