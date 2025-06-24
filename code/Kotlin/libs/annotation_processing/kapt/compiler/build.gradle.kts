plugins {
    alias(conventionLibs.plugins.jvmLibrary)
    alias(kotlinLibs.plugins.kapt)
}

dependencies {
    implementation(projects.libs.annotationProcessing.kapt.annotation)

    implementation(squareLibs.kotlinpoet)

    implementation(googleLibs.auto.service)
    kapt(googleLibs.auto.service)
}