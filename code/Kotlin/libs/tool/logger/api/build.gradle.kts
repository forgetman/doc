plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "logger.api"
}

dependencies {
    compileOnly(androidxLibs.annotation)
}