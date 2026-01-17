plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.va6corporation.kramviapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.va6corporation.kramviapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 39
        versionName = "4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField(type = "String", name = "BASE_URL", value = "\"http://192.168.18.101:3000/api/v2/\"")
            buildConfigField(type = "String", name = "BASE_FRONT_URL", value = "\"http://192.168.1.100:4200/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(type = "String", name = "BASE_URL", value = "\"https://app.kramvi.com/api/v2/\"")
            buildConfigField(type = "String", name = "BASE_FRONT_URL", value = "\"https://app.kramvi.com/\"")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.threetenabp)
    implementation(libs.converter.gson)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.itext7.core)
    implementation(libs.play.services.code.scanner)
    implementation(libs.escpos.thermalprinter.android)
    implementation(libs.app.update.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //room
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

}