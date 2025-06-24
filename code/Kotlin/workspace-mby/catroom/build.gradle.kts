import org.gradle.kotlin.dsl.implementation

plugins {
    alias(conventionLibs.plugins.android.app)
    alias(conventionLibs.plugins.android.flavors)
    alias(conventionLibs.plugins.androidx.hilt)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "catroom"

    defaultConfig {
        applicationId = "ys.reader.app"
        versionCode = 1_0_39
        versionName = "1.0.39"
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            keyAlias = "android"
            keyPassword = "Android"
            storeFile = file("./signed.jks")
            storePassword = "Android"
        }
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG_LOG", "true")
            buildConfigField("boolean", "OUTPUT_TO_SDCARD", "false")

            isShrinkResources = false
            isMinifyEnabled = false

            signingConfig = signingConfigs.named("release").get()
        }
        release {
            buildConfigField("boolean", "DEBUG_LOG", "false")
            buildConfigField("boolean", "OUTPUT_TO_SDCARD", "false")

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

    applicationVariants.configureEach {
        outputs.configureEach {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                val config = project.android.defaultConfig
                val versionName = config.versionName
                this.outputFileName = "猫屋_v${versionName}.apk"
            }
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin-bluetooth")
            java.srcDir("src/main/kotlin-mqtt")
        }
    }

    lint {
        // 每个机器都要生成自己的lint-baseline.xml文件(release版本)
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    api(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(projects.libs.vector.core)
    implementation(projects.libs.vector.databinding)
    implementation(projects.libs.tool.logger.xlog)
    implementation(projects.libs.tool.trigger)
    implementation(projects.libs.tool.bluetooth)
    kapt(projects.libs.annotationProcessing.kapt.compiler)

    // 腾讯IOT
    implementation(projects.workspaceMby.iot.explorer.deviceAndroid)

    implementation("com.herohan:UVCAndroid:1.0.7")
    implementation("com.github.pedroSG94.RootEncoder:library:2.5.0")

    implementation(googleLibs.gms.location)

    implementation(androidxLibs.work.runtime.ktx)
    implementation(androidxLibs.startup)

//    debugImplementation(squareLibs.leakcanary)
}