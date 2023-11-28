plugins {
    id("com.android.application")
}

android {
    namespace = "com.labs.lab2weather"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.labs.lab2weather"
        minSdk = 18
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        setProperty("archivesBaseName", applicationId + "-v" + versionCode + "(" + versionName + ")")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {

    implementation("com.squareup.retrofit2:retrofit:2.7.2")
    implementation("com.squareup.retrofit2:converter-gson:2.7.2")
    implementation("com.squareup.okhttp3:okhttp:3.6.0")
    implementation( "com.google.code.gson:gson:2.10.1" )

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-base:18.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}