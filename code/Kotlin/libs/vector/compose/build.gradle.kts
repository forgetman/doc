plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(conventionLibs.plugins.android.lint)
    alias(conventionLibs.plugins.androidx.compose)
}

android {
    namespace = "vector.app.compose"

    defaultConfig {
        consumerProguardFile("proguard-compose.pro")
    }

    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/kotlin-app",
            )
        }
    }
}

dependencies {
    api(projects.libs.vector.core)
}