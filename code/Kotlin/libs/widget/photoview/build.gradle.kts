plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "vector.widget.photoview"
}

dependencies {
    implementation(projects.libs.vector.androidview)
}