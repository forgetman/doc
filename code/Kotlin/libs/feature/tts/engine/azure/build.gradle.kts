plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(conventionLibs.plugins.androidx.media3)
}

android {
    namespace = "feature.tts.engine.azure"
    resourcePrefix = "azure"
}

dependencies {
    implementation(projects.libs.vector.core)
    implementation(projects.libs.feature.tts.core)
    implementation(projects.libs.feature.tts.processor)
    implementation(projects.libs.feature.media)
    implementation(projects.libs.tool.trigger)

    implementation(othersLibs.tts.azure)
}