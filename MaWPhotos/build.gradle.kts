plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation)
}

import java.util.Properties
import java.io.FileInputStream

val homedir = System.getProperty("user.home")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(homedir + "/.gradle/gradle.properties"))

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    namespace = "us.mikeandwan.photos"

    defaultConfig {
        applicationId = "us.mikeandwan.pictures"
        minSdk = 31
        targetSdk = 34
        versionCode = 56
        versionName = "7.4"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        manifestPlaceholders["appAuthRedirectScheme"] = "us.mikeandwan.photos"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()

        freeCompilerArgs = listOf(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    kotlin {
        jvmToolchain(17)
    }

    signingConfigs {
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
                "proguard-jackson.pro",
                "proguard-maw.pro",
                "proguard-retrofit2.pro"
            )
        }
    }

    packagingOptions {
        resources {
            excludes += listOf("META-INF/LICENSE", "META-INF/NOTICE")
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    // core
    implementation(libs.core.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // jetpack
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.layout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.room)
    implementation(libs.androidx.swiperefresh)
    implementation(libs.androidx.viewpager)
    implementation(libs.androidx.work)
    kapt(libs.hilt.compiler)
    ksp(libs.androidx.room.compiler)

    // flexbox
    implementation(libs.flexbox)

    // hilt
    implementation(libs.hilt)
    kapt(libs.hilt.android.compiler)
    androidTestImplementation(libs.hilt.testing)
    kaptAndroidTest(libs.hilt.compiler.kapt)
    testImplementation(libs.hilt.testing)
    kaptTest(libs.hilt.compiler.kapt)

    // markdown
    implementation(libs.markdown)

    // material
    implementation(libs.material)

    // appauth
    implementation(libs.appauth)

    // okhttp
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.jackson)

    // glide
    implementation(libs.glide)
    implementation(libs.glide.okhttp)
    ksp(libs.glide.compiler)

    // photoview
    implementation(libs.photoview)

    // timber
    implementation(libs.timber)

    // testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}
