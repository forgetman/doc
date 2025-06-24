plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "coroutine"
}

dependencies {
    implementation(androidxLibs.bundles.lifecycle)
}