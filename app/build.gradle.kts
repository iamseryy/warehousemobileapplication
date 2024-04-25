plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    kotlin("plugin.serialization") version "1.9.22"
}
android {
    namespace = "ru.bz.mobile.inventory"
    compileSdk = 34

    /* Valid values major - [0,21000); minor и patch - [0, 99) */
    val major = 1
    val minor = 0
    val patch = 0
    val postfix = ""

    defaultConfig {
        applicationId = "ru.bz.mobile.inventory"
        minSdk = 25
        targetSdk = 33
        versionCode = major * 10000 + minor * 100 + patch
        versionName = "${major}.${minor}.${patch}${postfix}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}
val String.version: String
    get() =rootProject.extra[this].toString()

dependencies {
//    implementation("androidx.core:core-ktx:${"coreVersion".version}")
    implementation("androidx.appcompat:appcompat:${"appCompatVersion".version}")
    implementation("androidx.activity:activity-ktx:${"activityVersion".version}")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    // Scanner
    implementation(files("libs/DataCollection.aar"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    // Room components
    implementation ("androidx.room:room-ktx:${"roomVersion".version}")
    implementation("androidx.datastore:datastore-core:1.0.0")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.navigation:navigation-fragment:2.7.4")
    ksp("androidx.room:room-compiler:${"roomVersion".version}")
    androidTestImplementation ("androidx.room:room-testing:${"roomVersion".version}")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${"lifecycleVersion".version}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${"lifecycleVersion".version}")
    implementation("androidx.lifecycle:lifecycle-common-java8:${"lifecycleVersion".version}")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    // Kotlin components
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${"coroutinesVersion".version}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${"coroutinesVersion".version}")

    implementation("com.google.android.material:material:${"materialVersion".version}")
    implementation("androidx.constraintlayout:constraintlayout:${"constraintLayoutVersion".version}")

    // Use this dependency to bundle the model with your app
    implementation("com.google.mlkit:barcode-scanning:${"mlkitBarcodeVersion".version}")


    testImplementation("junit:junit:${"junitVersion".version}")
    androidTestImplementation("androidx.test.ext:junit:${"androidxJunitVersion".version}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${"espressoVersion".version}")

    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    implementation("androidx.navigation:navigation-fragment-ktx:${"navigationUIVersion".version}")
    implementation("androidx.navigation:navigation-ui-ktx:${"navigationUIVersion".version}")

    implementation("com.google.code.gson:gson:2.9.0")
}


