plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace =  "logger.logcat"
}

dependencies {
    compileOnly(androidxLibs.annotation)
    implementation(projects.libs.tool.logger.api)
}