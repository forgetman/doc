plugins {
    alias(conventionLibs.plugins.android.app)
    alias(conventionLibs.plugins.android.flavors)
    alias(conventionLibs.plugins.androidx.compose)
    alias(conventionLibs.plugins.androidx.hilt)
    alias(conventionLibs.plugins.androidx.media3)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "test.compose"

    defaultConfig {
        applicationId = "ys.test.compose.app"
        versionCode = 1_0_0
        versionName = "1.0.0"
    }

    buildFeatures {
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

            signingConfig = signingConfigs.named("release").get()

            ndk {
                abiFilters.add("armeabi-v7a")
            }
        }
    }
}

dependencies {
    implementation(projects.libs.vector.compose)
    implementation(projects.libs.tool.logger.logcat)

    implementation(projects.libs.feature.media)
    implementation(projects.libs.feature.tts.core)
    implementation(projects.libs.feature.tts.engine.azure)

    kapt(projects.libs.annotationProcessing.kapt.compiler)

    implementation(squareLibs.okhttp3.ok)
    implementation(squareLibs.okhttp3.logging)

    implementation(androidxLibs.paging.runtime)
    implementation(androidxLibs.paging.common)
    implementation(androidxLibs.paging.compose)
}