plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(conventionLibs.plugins.androidx.media3)
}

android {
    namespace = "feature.media"
}

dependencies {
    implementation(projects.libs.vector.core)
}