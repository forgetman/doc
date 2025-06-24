plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "lib.base"
}

dependencies {
    api(projects.libs.vector.core)
    api(projects.libs.vector.compose)
    implementation(projects.libs.tool.logger.logcat)
    kapt(projects.libs.annotationProcessing.kapt.compiler)
}
