plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "eth.okhttp"

    defaultConfig {
        consumerProguardFiles("proguard-ok.pro")
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin-okhttp")
        }
    }
}

dependencies {
    compileOnly(androidxLibs.annotation)
    implementation(androidxLibs.bundles.lifecycle)

    implementation(squareLibs.okhttp3.ok)

    api(projects.libs.network.eth.api)
    implementation(projects.libs.network.okhttpExt)

    implementation(projects.libs.tool.sugar)
    implementation(projects.libs.eson)
}