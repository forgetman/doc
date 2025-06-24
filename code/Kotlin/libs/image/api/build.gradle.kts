plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "image.api"
}

dependencies {
    compileOnly(androidxLibs.annotation)
    implementation(androidxLibs.core.ktx)
    implementation(androidxLibs.lifecycle.runtime)
}