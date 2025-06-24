plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "aop.annotation"
}

dependencies {
    compileOnly(androidxLibs.annotation)
}