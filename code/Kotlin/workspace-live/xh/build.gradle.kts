plugins {
    alias(conventionLibs.plugins.android.app)
    alias(conventionLibs.plugins.android.flavors)
    alias(conventionLibs.plugins.androidx.hilt)
    alias(conventionLibs.plugins.androidx.room)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "star"

    defaultConfig {
        applicationId = "star.live.app"
        versionCode = 1
        versionName = "1.1"

        manifestPlaceholders["app_name"] = "星皓互娱"
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            keyAlias = "reader"
            keyPassword = "812024"
            storeFile = file("./ys.jks")
            storePassword = "812024"
        }
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG_LOG", "true")

            isShrinkResources = false
            isMinifyEnabled = false

            signingConfig = signingConfigs.named("release").get()
        }
        release {
            buildConfigField("boolean", "DEBUG_LOG", "false")

            isShrinkResources = true
            isMinifyEnabled = true

            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )

            signingConfig = signingConfigs.named("release").get()

            ndk {
                abiFilters.add("armeabi-v7a")
            }
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin-databinding")
        }
    }
}

dependencies {
    implementation(projects.libs.vector.core)
    implementation(projects.libs.vector.databinding)
    implementation(projects.libs.tool.logger.logcat)
    kapt(projects.libs.annotationProcessing.kapt.compiler)

    implementation(projects.libs.bus.flowbus)

    debugImplementation(squareLibs.leakcanary)
}