plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvmToolchain(19)

    androidTarget()

    // iOS targets — no framework needed here; iosApp uses ComposeApp framework from :composeApp.
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            api(libs.firebase.auth)
            api(libs.firebase.firestore)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "pl.quizpszczelarski.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}
