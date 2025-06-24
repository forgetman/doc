plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "vector.app.databinding"

    defaultConfig {
        consumerProguardFile("proguard-databinding.pro")
    }

    buildFeatures {
        dataBinding = true
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin-bindingadapter")
        }
    }
}

dependencies {
    implementation(projects.libs.vector.androidview)
}