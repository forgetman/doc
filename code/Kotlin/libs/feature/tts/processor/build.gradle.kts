plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(conventionLibs.plugins.androidx.media3)
}

android {
    namespace = "feature.tts.processor"

    sourceSets {
        getByName("main") {
            jniLibs.srcDir("libs")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(projects.libs.vector.core)
    implementation(projects.libs.feature.tts.core)
}