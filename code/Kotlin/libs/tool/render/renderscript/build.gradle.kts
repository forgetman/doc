@file:Suppress("UnstableApiUsage")

plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "com.google.android.renderscript"

    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags.add("-std=c++17")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(androidxLibs.core.ktx)
}