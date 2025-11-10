// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // Firebase Database
    id("com.google.gms.google-services") version "4.4.2" apply false

    // Dependency injection with Hilt
    id("com.google.dagger.hilt.android") version "2.55" apply false

}

// Navigation
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        val nav_version = "2.8.6"
        val kotlin_version = "2.1.10"

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}