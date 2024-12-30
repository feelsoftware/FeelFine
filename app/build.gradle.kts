import java.io.File
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.feelsoftware.feelfine"
    compileSdk = 35

    val props = Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "local.properties")))
    }

    defaultConfig {
        applicationId = "com.feelsoftware.feelfine"
        minSdk = 23
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = props.getProperty("versionCode").toInt()
        versionName = props.getProperty("versionName")
        resourceConfigurations.add("en")
    }

    signingConfigs {
        getByName("debug") {
            storeFile = File(rootDir, "app/debug.jks")
            storePassword = "V7e5tBA2"
            keyAlias = "feelsoftware"
            keyPassword = "xVr8uud5"
        }
        create("release") {
            storeFile = File(props.getProperty("signing.storeFile"))
            storePassword = props.getProperty("signing.storePassword")
            keyAlias = props.getProperty("signing.keyAlias")
            keyPassword = props.getProperty("signing.keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
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
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
        }
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // ArchComponents
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.common.java8)
    implementation(libs.lifecycle.runtime.ktx)

    // NavComponent
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Compose
    implementation(libs.activity.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.test.manifest)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)

    // Accompanist
    implementation(libs.accompanist.pager)

    // Firebase
    implementation(libs.play.services.auth)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)

    // Google Fit
    implementation(libs.play.services.fitness)
    implementation(libs.google.api.client)
    implementation(libs.google.api.client.android)
    implementation(libs.google.api.services.fitness)

    // DataBase
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.rxjava3)

    // RxJava
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.rxrelay)

    // WorkManager
    implementation(libs.work.runtime.ktx)
    implementation(libs.work.rxjava3)

    // DI
    implementation(libs.koin.android)

    // Utils
    implementation(libs.timber)

    // UI
    implementation(libs.mpandroidchart)
    implementation(libs.circularprogressbar)
    implementation(libs.pdf.viewer)

    // Fix for ListenableFuture
    implementation(libs.listenablefuture)
}