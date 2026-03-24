plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.kotlin.compose)

}

android {
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    namespace = "com.example.sky_track_mobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sky_track_mobile"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        val apiKey = project.findProperty("AVIATIONSTACK_API_KEY") as? String ?: ""
        debug {
            buildConfigField("String", "AVIATIONSTACK_API_KEY", "\"$apiKey\"")
        }
        release {
            buildConfigField("String", "AVIATIONSTACK_API_KEY", "\"$apiKey\"")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)


    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)


    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)


    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)


    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core.ktx)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}