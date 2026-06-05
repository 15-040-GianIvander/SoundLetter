import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildkonfig)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

kotlin {
    // FIX 1: Registrasi Android Target secara eksplisit
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    jvmToolchain(17)

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.datastore.preferences)
            implementation(libs.okio)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
        }
    }
}

// FIX 2: Konfigurasi BuildKonfig yang robust (Hapus exposeObject)
buildkonfig {
    packageName = "com.soundletter.app"
    defaultConfigs {
        val jamendoId = (localProperties.getProperty("JAMENDO_CLIENT_ID") ?: "cbb32072").replace("\"", "")
        val geminiKey = (localProperties.getProperty("GEMINI_API_KEY") ?: "").replace("\"", "")

        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "JAMENDO_CLIENT_ID", "\"$jamendoId\"")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "GEMINI_API_KEY", "\"$geminiKey\"")
    }
}

android {
    namespace = "com.soundletter.app"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.soundletter.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures { buildConfig = true }
}

// FIX 3: Tambahkan konfigurasi database SQLDelight agar tidak warning
sqldelight {
    databases {
        create("SoundLetterDatabase") {
            packageName.set("com.soundletter.app.data.local")
        }
    }
}
