plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
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
