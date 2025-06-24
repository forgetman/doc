plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "trigger"
}

dependencies {
    compileOnly(androidxLibs.annotation)
    implementation(androidxLibs.core.ktx)

    implementation(projects.libs.tool.compat)
    implementation(projects.libs.tool.sugar)
    implementation(projects.libs.tool.coroutine)
}