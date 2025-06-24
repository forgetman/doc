plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(conventionLibs.plugins.android.lint)
}

android {
    namespace = "vector.app.androidview"

    defaultConfig {
        consumerProguardFile("proguard-androidview.pro")
    }

    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/kotlin-app",
                "src/main/kotlin-compat",
                "src/main/kotlin-widget"
            )
        }
    }
}

dependencies {
    api(projects.libs.vector.core)
    api(projects.libs.image.api)
    implementation(projects.libs.image.coil)
    implementation(projects.libs.tool.render.aar)
}