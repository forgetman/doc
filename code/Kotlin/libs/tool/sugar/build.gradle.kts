plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "sugar"
}

dependencies {
    implementation(androidxLibs.core.ktx)
    implementation(androidxLibs.bundles.lifecycle)
    api(projects.libs.tool.logger.api)
}