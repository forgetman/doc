plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "image.coil"
}

dependencies {
    implementation(androidxLibs.bundles.lifecycle)
    implementation(coilLibs.bundles.coil)
    implementation(coilLibs.okhttp)

    implementation(projects.libs.tool.sugar)
    implementation(projects.libs.tool.coroutine)
    implementation(projects.libs.image.api)
}