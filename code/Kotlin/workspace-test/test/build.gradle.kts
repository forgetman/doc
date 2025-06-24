plugins {
    alias(conventionLibs.plugins.android.app)
    alias(conventionLibs.plugins.android.flavors)
    alias(conventionLibs.plugins.androidx.media3)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "test"

    defaultConfig {
        applicationId = "ys.test.app"
        versionCode = 1_0_0
        versionName = "1.0.0"

        kapt {
            arguments {
                arg("AROUTER_MODULE_NAME", project.name)
            }
        }
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
    api(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(projects.libs.vector.androidview)
    implementation(projects.libs.vector.databinding)

    implementation(projects.libs.widget.scrollable)
    implementation(projects.libs.widget.viewpager)
    implementation(projects.libs.widget.widget)

    implementation(projects.libs.tool.bluetooth)
    implementation(projects.libs.tool.logger.logcat)
    implementation(projects.libs.tool.trigger)
    kapt(projects.libs.annotationProcessing.kapt.compiler)

    implementation(airbnbLibs.lottie.compose)

    debugImplementation(squareLibs.leakcanary)

    implementation(projects.libs.bus.flowbus)

    implementation(squareLibs.okhttp3.ok)
    implementation(squareLibs.okhttp3.logging)

    implementation(squareLibs.retrofit.core)
    implementation("com.github.chenxyu:retrofit-adapter-flow:1.0.4")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

//    implementation(squareLibs.retrofit.core) {
//        exclude(group = "com.android.support", module = "appcompat")
//    }
//    implementation("com.cv4j:rxcv4j:0.1.1.1") {
//        exclude(group = "com.android.support", module = "appcompat")
//    }

    implementation(rxLibs.rx3.kotlin)
    implementation(rxLibs.rx3.android)

    implementation(bleLibs.scanner)
    implementation(bleLibs.core)
    implementation(bleLibs.ktx)
    implementation(bleLibs.common)

    implementation(androidxLibs.media)

//    implementation(projects.libs.aop.runtime)
}