plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "bluetooth"

    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/kotlin-bluetooth",
                "src/main/kotlin-bluetoothle",
            )
        }
    }
}

dependencies {
    implementation(androidxLibs.bundles.lifecycle)
    implementation(projects.libs.tool.compat)
    implementation(projects.libs.tool.coroutine)
    implementation(projects.libs.tool.sugar)

    implementation(bleLibs.scanner)
    implementation(bleLibs.core)
}