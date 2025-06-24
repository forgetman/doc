import convention.ext.FlavorDimension

plugins {
    alias(conventionLibs.plugins.android.app)
//    alias(conventionLibs.plugins.android.flavors)
    alias(conventionLibs.plugins.androidx.hilt)
    alias(conventionLibs.plugins.androidx.room)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "reader"

    defaultConfig {
        applicationId = "ys.reader.app"
        versionCode = 218
        versionName = "2.1.8"

        flavorDimensions += FlavorDimension.contentType.name

        manifestPlaceholders["app_name"] = "安九阅读"
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
            buildConfigField("boolean", "TEST", "true")

            isShrinkResources = false
            isMinifyEnabled = false

            signingConfig = signingConfigs.named("release").get()
        }
        release {
            buildConfigField("boolean", "DEBUG_LOG", "false")
            buildConfigField("boolean", "TEST", "false")

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

    productFlavors {
        create("demo") {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["app_name"] = "安九阅读(B)"
        }
        create("prod")
    }

    applicationVariants.configureEach {
        outputs.configureEach {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                val config = project.android.defaultConfig
                val versionName = config.versionName
                this.outputFileName = "安九阅读_${versionName}.apk"
            }
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin-databinding")
            res.srcDir("src/main/res-night")
        }
    }
}

dependencies {
    implementation(projects.libs.vector.androidview)
    implementation(projects.libs.vector.databinding)

    implementation(projects.libs.widget.scrollable)
    implementation(projects.libs.widget.swiperefresh)
    implementation(projects.libs.widget.widget)
    implementation(projects.libs.widget.viewpager)

    implementation(projects.libs.tool.logger.logcat)
    implementation(projects.libs.bus.flowbus)
    kapt(projects.libs.annotationProcessing.kapt.compiler)

    implementation(androidxLibs.cardview)


    implementation(airbnbLibs.lottie)

    debugImplementation(squareLibs.leakcanary)
}