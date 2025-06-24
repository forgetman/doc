plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "bus.flow"
}

dependencies {
    implementation(androidxLibs.bundles.lifecycle)

    api(projects.libs.bus.busapi)
    implementation(projects.libs.tool.coroutine)
}