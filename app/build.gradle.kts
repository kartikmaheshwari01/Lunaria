plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.lunarphase"
    compileSdk = 35



    defaultConfig {
        applicationId = "com.example.lunarphase"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true


    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.android.volley:volley:1.2.1")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("androidx.work:work-runtime:2.9.0")





    implementation("com.airbnb.android:lottie:6.3.0")


    // The view calendar library for Android
    implementation("com.kizitonwose.calendar:view:2.7.0")

    // The compose calendar library for Android
    implementation("com.kizitonwose.calendar:compose:2.7.0")


    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.1.3")

}
configurations.all {
    resolutionStrategy {
        force("androidx.core:core-ktx:1.12.0")
    }
}