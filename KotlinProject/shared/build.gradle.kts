
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    // iOS targets
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    // JVM target
    jvm()

    // Android target
    android {
        namespace = "org.example.project.shared"

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

        // Temporarily removed device-test configuration
        // until the Gradle compatibility issue is resolved.
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)

                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                implementation(compose.materialIconsExtended)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.compose.uiTooling)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.google.zxing:core:3.5.4")
                implementation("com.google.zxing:javase:3.5.4")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        // Temporarily removed androidDeviceTest dependency.
    }
}
