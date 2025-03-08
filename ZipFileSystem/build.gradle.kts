plugins {
    id("com.android.library")
}

android {
    namespace = "com.sun.nio.zipfs"
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
}