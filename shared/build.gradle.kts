import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val networkBaseUrl = "https://api.example.com"
val generatedNetworkConfigDir = layout.buildDirectory.dir("generated/networkConfig/commonMain/kotlin")
val generateNetworkConfig by tasks.registering {
    inputs.property("networkBaseUrl", networkBaseUrl)
    outputs.dir(generatedNetworkConfigDir)

    doLast {
        val configFile = generatedNetworkConfigDir
            .get()
            .file("com/maghraby/kmp_template/di/NetworkConfig.kt")
            .asFile
        configFile.parentFile.mkdirs()
        configFile.writeText(
            """
            package com.maghraby.kmp_template.di

            internal const val NETWORK_BASE_URL = "$networkBaseUrl"
            """.trimIndent()
        )
    }
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    android {
       namespace = "com.maghraby.kmp_template.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    sourceSets {
        commonMain {
            kotlin.srcDir(generateNetworkConfig)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.bundles.koin)
            implementation(libs.navigation3.ui)
            implementation(libs.kotlinx.serialization.core)
            implementation(project(":core:navigation"))
            implementation(project(":core:network"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.testRunner)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}