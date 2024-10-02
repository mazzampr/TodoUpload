plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  id ("androidx.navigation.safeargs")
  id("com.google.gms.google-services")
}

android {
  namespace = "com.nakama.todoupload"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.nakama.todoupload"
    minSdk = 21
    targetSdk = 34
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
  buildFeatures {
    viewBinding = true
    compose = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.0"
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {

  implementation(project(":core"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.navigation.fragment.ktx)
  implementation(libs.androidx.navigation.ui.ktx)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)

  // Firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.auth)
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.storage)

  // Koin
  implementation(libs.koin.android)

  // Glide
  implementation(libs.glide)

  // Circle Image View
  implementation(libs.circleimageview)

  // Lifecycle
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.lifecycle.livedata.ktx)

  // Compose Implementation
  implementation ("androidx.compose.ui:ui")
  implementation ("androidx.compose.material:material")
  implementation ("androidx.compose.runtime:runtime")
  implementation ("androidx.activity:activity-compose:1.7.2")
  implementation (platform("androidx.compose:compose-bom:2022.10.00"))
  implementation ("androidx.compose.ui:ui-graphics")
  implementation ("androidx.compose.ui:ui-tooling-preview")

  // Exoplayer
  implementation("androidx.media3:media3-exoplayer:1.3.0")
  implementation("androidx.media3:media3-exoplayer-hls:1.3.0")
  implementation("androidx.media3:media3-ui:1.3.0")
  implementation("androidx.media3:media3-session:1.3.0")
  implementation("androidx.media3:media3-transformer:1.3.0")
  implementation("androidx.media3:media3-muxer:1.3.0")
  implementation("androidx.media3:media3-effect:1.3.0")
  implementation("androidx.media3:media3-common:1.3.0")

  // Calendar view
  implementation ("com.github.kizitonwose:CalendarView:1.0.3")
  
  // Pick Image
  implementation ("com.github.jrvansuita:PickImage:3.0.2")

  // Coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

  implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

  // Swipe detector
  implementation ("it.xabaras.android:recyclerview-swipedecorator:1.4")
}