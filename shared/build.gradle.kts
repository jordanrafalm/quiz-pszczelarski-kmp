plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
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
            implementation(libs.sqldelight.coroutines)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

sqldelight {
    databases {
        create("QuizDatabase") {
            packageName.set("pl.quizpszczelarski.shared.data.local.db")
            // Version 1: QuestionEntity + SyncMeta + PendingScore
            // Future schema changes: add numbered .sqm migration files (1.sqm, 2.sqm, etc.)
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/migrations"))
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
