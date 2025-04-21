plugins {
    alias(commonLibs.plugins.android.app) apply false

    alias(googleLibs.plugins.ksp) apply false
    alias(googleLibs.plugins.google.services) apply false
    alias(googleLibs.plugins.firebase.crashlytics) apply false

    alias(kotlinLibs.plugins.android) apply false
    alias(kotlinLibs.plugins.compose) apply false

    alias(hiltLibs.plugins.android) apply false

    alias(androidxLibs.plugins.room) apply false

    alias(conventionLibs.plugins.initial) apply true
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}