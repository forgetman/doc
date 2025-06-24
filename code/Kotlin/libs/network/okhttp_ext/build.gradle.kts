plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "okhttp.ext"
}

dependencies {
    implementation(androidxLibs.bundles.lifecycle)
    implementation(squareLibs.okhttp3.ok)
    implementation(projects.libs.tool.compat)
}