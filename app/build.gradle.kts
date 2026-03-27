plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    plugin google
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proyecto_ubi_tiempo_real_famm"
    compileSdk = 33 //ANDROID 13

    defaultConfig {
        applicationId = "com.example.proyecto_ubi_tiempo_real_famm"
        minSdk = 24 //ANDROID 7
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.activity:activity:1.12.4")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    //CONTROLA LAS VERSIONES DE FIREBASE PARA QUE NO CHOQUEN ENTRE SI
    implementation(platform("com.google.firebase:firebase-bom:34.9.0"))
    //LIBRERIA DE FIREBASE
    implementation("com.google.firebase:firebase-database")
    //HERRAMIENTA DE GPS DE GOOGLE
    implementation("com.google.android.gms:play-services-location:21.2.0")
    //LIBRERIA DE MAPBOX
    implementation("com.mapbox.maps:android:11.2.0")
}