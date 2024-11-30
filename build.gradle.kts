// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false // Używamy nowszej wersji Google Services
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Jeśli potrzebujesz innych zależności na poziomie projektu, dodaj je tutaj.
    }
}
configurations.all {
    resolutionStrategy {
        force("androidx.core:core-ktx:1.13.1")
        force("androidx.core:core:1.13.1")
    }
}

