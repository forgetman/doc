plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "bus.api"
}

dependencies {
    implementation(androidxLibs.bundles.lifecycle)
}