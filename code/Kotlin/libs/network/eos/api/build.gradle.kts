plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "eos"

    defaultConfig {
        consumerProguardFiles("proguard-eos.pro")
    }
}

dependencies {
    implementation(projects.libs.tool.sugar)
    implementation(projects.libs.tool.coroutine)

    implementation(androidxLibs.bundles.lifecycle)
}