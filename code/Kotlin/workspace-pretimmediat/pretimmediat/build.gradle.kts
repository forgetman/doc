plugins {
    alias(conventionLibs.plugins.android.app)
    alias(conventionLibs.plugins.android.flavors)
    alias(conventionLibs.plugins.android.firebase)
    alias(conventionLibs.plugins.androidx.hilt)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "pretimmediat"

    defaultConfig {
        applicationId = "com.bear.young.college"

        versionCode = 1_0_20
        versionName = "1.0.20"

        manifestPlaceholders["app_name"] = "Prêt immédiat"
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            keyAlias = "pretimmediat"
            keyPassword = "pretimmediat"
            storeFile = file("./pretimmediat.jks")
            storePassword = "pretimmediat"
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

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.named("release").get()

            bundle {
                language {
                    enableSplit = false
                }
                density {
                    enableSplit = false
                }
                abi {
                    enableSplit = false
                }
            }
        }
    }

    applicationVariants.configureEach {
        val buildTypeName = this.buildType.name
        outputs.configureEach {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                val config = project.android.defaultConfig
                val versionName = config.versionName
                this.outputFileName = "pretimmediat_${versionName}_${buildTypeName}.apk"
            }
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin-databinding", "src/main/kotlin-test")
            res.srcDirs("src/main/res-test")
        }
    }

    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }
}

dependencies {
    implementation(projects.libs.vector.androidview)
    implementation(projects.libs.vector.databinding)

    implementation(projects.libs.widget.scrollable)
    implementation(projects.libs.widget.swiperefresh)
    implementation(projects.libs.widget.viewpager)
    implementation(projects.libs.widget.widget)

    implementation(projects.libs.tool.logger.logcat)
    implementation(projects.libs.bus.flowbus)
    kapt(projects.libs.annotationProcessing.kapt.compiler)

    implementation(googleLibs.gms.location)

    // Facebook Core only (Analytics)
    implementation("com.facebook.android:facebook-core:18.0.3")

    // appsFlyer
    implementation("com.appsflyer:af-android-sdk:6.17.0")
    implementation("com.android.installreferrer:installreferrer:2.2")

    implementation("com.google.android.play:review-ktx:2.0.1")

    debugImplementation(squareLibs.leakcanary)
}