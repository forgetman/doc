plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "eson"

    defaultConfig {
        consumerProguardFile("proguard-gson.pro")
    }
}

dependencies {
    implementation(androidxLibs.bundles.lifecycle)

    implementation(projects.libs.tool.live)
    implementation(projects.libs.tool.sugar)

    api(googleLibs.gson)
}