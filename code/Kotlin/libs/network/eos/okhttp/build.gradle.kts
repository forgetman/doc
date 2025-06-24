plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "eos.okhttp"

    defaultConfig {
        consumerProguardFiles("proguard-ok.pro")
    }
}

dependencies {
    api(projects.libs.network.eos.api)
    implementation(projects.libs.network.okhttpExt)
    
    implementation(squareLibs.okhttp3.ok)
}