plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "logger.logan"
}

dependencies {
    compileOnly(androidxLibs.annotation)
    implementation(projects.libs.tool.logger.api)
    implementation(othersLibs.log.logan)
}