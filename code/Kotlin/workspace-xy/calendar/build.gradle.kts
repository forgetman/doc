plugins {
    alias(conventionLibs.plugins.android.app)
    alias(conventionLibs.plugins.android.flavors)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "xy.calendar"

    defaultConfig {
        applicationId = "xy.calendar.app"
        versionCode = 330
        versionName = "3.3"
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        getByName("main") {
            res.srcDir("src/main/res-widget")
        }
    }
}

dependencies {
    implementation(projects.workspaceXy.libs.libBase)
    kapt(projects.libs.annotationProcessing.kapt.compiler)
}