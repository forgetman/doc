plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(conventionLibs.plugins.aop)
}

android {
    namespace = "aop.runtime"

    defaultConfig {
        consumerProguardFiles("proguard-aop.pro")
    }
}

dependencies {
    compileOnly(androidxLibs.annotation)
    api(aopLibs.aspectj.jrt)
    api(projects.libs.aop.annotation)
}