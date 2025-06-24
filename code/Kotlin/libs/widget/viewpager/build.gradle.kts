plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "vector.widget.viewpager"

    buildFeatures {
        dataBinding = true
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin-databinding")
        }
    }
}

dependencies {
    implementation(projects.libs.vector.androidview)
    implementation(projects.libs.vector.databinding)
    implementation(projects.libs.widget.scrollable)
}