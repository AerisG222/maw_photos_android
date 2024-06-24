import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.androidx.room)
}

android {
    compileSdk = 34
    namespace = "us.mikeandwan.photos"

    defaultConfig {
        applicationId = "us.mikeandwan.pictures"
        minSdk = 26
        targetSdk = 34
        versionCode = 57
        versionName = "8.0"

        manifestPlaceholders["appAuthRedirectScheme"] = "us.mikeandwan.photos"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            // freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    // https://developer.android.com/training/data-storage/room/migrating-db-versions#kotlin_2
    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    signingConfigs {
        val homedir = System.getProperty("user.home")
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream("$homedir/.gradle/gradle.properties"))

        create("release") {
            storeFile = file(keystoreProperties["RELEASE_STORE_FILE"] as String)
            storePassword = keystoreProperties["RELEASE_STORE_PASSWORD"] as String
            keyAlias = keystoreProperties["RELEASE_KEY_ALIAS"] as String
            keyPassword = keystoreProperties["RELEASE_KEY_PASSWORD"] as String
        }
    }

    flavorDimensions += listOf("dev_or_prod")

    productFlavors {
        create("development") {
            dimension = "dev_or_prod"
        }

        create("production") {
            dimension = "dev_or_prod"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            signingConfig = signingConfigs["release"]

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-maw.pro"
            )
        }
    }

    packaging {
        resources {
            excludes += listOf("META-INF/LICENSE", "META-INF/NOTICE")
        }
    }
}

composeCompiler {
    enableStrongSkippingMode = true
}

dependencies {
    implementation(libs.androidx.profileinstaller)

    "baselineProfile"(project(":baselineprofile"))

    implementation(libs.jetbrains.kotlin.stdlib)
    implementation(libs.jetbrains.coroutines.android)
    implementation(libs.jetbrains.kotlinx.serialization.json)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.hilt.navigation)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.okhttp)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room)
    implementation(libs.androidx.work)

    ksp(libs.androidx.hilt.compiler)
    ksp(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.google.hilt)
    ksp(libs.google.hilt.android.compiler)

    implementation(libs.appauth)
    implementation(libs.coil)
    implementation(libs.compose.ratingbar)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.markdown)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.timber)
    implementation(libs.zoomable)

    testImplementation(libs.junit)
    testImplementation(libs.google.hilt.testing)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.google.hilt.testing)
}

hilt {
    enableAggregatingTask = true
}
