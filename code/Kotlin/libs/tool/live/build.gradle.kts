plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "live"
}

dependencies {
    implementation(androidxLibs.bundles.lifecycle)
    implementation(projects.libs.tool.sugar)
    implementation(projects.libs.tool.coroutine)
}