plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "logger.xlog"

    defaultConfig {
        consumerProguardFile("proguard-xlog.pro")
    }
}

dependencies {
    compileOnly(androidxLibs.annotation)
    implementation(projects.libs.tool.logger.api)
    implementation(othersLibs.log.xlog)
}