plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "eth.api"

    defaultConfig {
        consumerProguardFiles("proguard-coroutines.pro", "proguard-eth.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin-coroutines")
        }
    }
}

dependencies {
    compileOnly(androidxLibs.annotation)
    implementation(androidxLibs.bundles.lifecycle)

    implementation(projects.libs.tool.compat)
    implementation(projects.libs.tool.sugar)
    implementation(projects.libs.tool.coroutine)
    implementation(projects.libs.eson)
}