plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.tungsten.fcllibrary"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    lint {
        targetSdk = 28
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            initWith(getByName("debug"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":FCLauncher"))
    implementation(project(":FCLCore"))
    implementation("commons-io:commons-io:2.15.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.asynclayoutinflater:asynclayoutinflater:1.0.0")
    implementation("net.fornwall:jelf:0.9.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}