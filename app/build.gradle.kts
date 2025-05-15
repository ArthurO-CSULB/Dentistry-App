plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}


android {
    namespace = "com.example.dentalhygiene"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dentalhygiene"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildFeatures {
            buildConfig = true
        }
        //Allows API to be called with BuildConfig in other project files
        buildConfigField("String", "MAPS_API_KEY", "\"${project.findProperty("MAPS_API_KEY") ?: ""}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        //isCoreLibraryDesugaringEnabled = true // Desugaring (PROBLEM TO HIGHLIGHT)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

secrets {
    // To add the Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
}


dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")

    // For navigating between pages.
    implementation("androidx.navigation:navigation-compose:2.8.4")

    implementation("androidx.test.services:storage:1.5.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.5")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.11.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Firebase
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Coroutines to help manage long-running tasks that may block the main thread and cause
    // the app to become unresponsive.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // APIs for handling runtime permissions in Jetpack Compose, which streamlines the process
    // for requesting permissions. Will be used for the timer and may be used for other features.
    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")

    // Sync
    // Filament dependencies for rendering a tooth model.
    //implementation("com.google.android.filament:filament-android:1.36.0")
    //implementation("com.google.android.filament:gltfio-android:1.36.0") // Loads GLB models
    //implementation("com.google.android.filament:filamat-android:1.36.0") // Material management

    // Webkit to work with modern WebView APIs. This is to for the teeth model.
    implementation("androidx.webkit:webkit:1.12.1")

    // Gson to parse JSON data for the dental hygiene trivia.
    implementation("com.google.code.gson:gson:2.10.1")

    // Maps SDK for Android
    // Google Maps Compose library
    val mapsComposeVersion = "4.4.1"
    implementation("com.google.maps.android:maps-compose:$mapsComposeVersion")
    // Google Maps Compose utility library
    implementation("com.google.maps.android:maps-compose-utils:$mapsComposeVersion")
    // Google Maps Compose widgets library
    implementation("com.google.maps.android:maps-compose-widgets:$mapsComposeVersion")
    // Places API
    implementation("com.google.android.libraries.places:places:3.3.0")
    //Coil for AsyncImage (for clinic photos)
    implementation("io.coil-kt:coil-compose:2.1.0")

    // For Product Recommendations
    // Converts data type to Kotlin-friendly ones
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
}