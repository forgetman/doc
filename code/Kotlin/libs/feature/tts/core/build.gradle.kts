plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "feature.tts.core"
}

dependencies {
    implementation(projects.libs.vector.core)

    implementation(othersLibs.disklrucache)
}