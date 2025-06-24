plugins {
    alias(conventionLibs.plugins.android.lib)
}

android {
    namespace = "compat"

    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/kotlin-telephony",
                "src/main/kotlin-signalStrength",
                "src/main/kotlin-network",
                "src/main/kotlin-x509",
                "src/main/kotlin-pendingIntent",
                "src/main/kotlin-packageManager",
                "src/main/kotlin-context",
                "src/main/kotlin-bluetooth",
                "src/main/kotlin-account",
                "src/main/kotlin-subscriptionManager",
                "src/main/kotlin-intent",
                "src/main/kotlin-inputMethod",
                "src/main/kotlin-thread",
                "src/main/kotlin-window",
            )
        }
    }
}

dependencies {
    compileOnly(androidxLibs.annotation)
    implementation(androidxLibs.core.ktx)
    implementation(androidxLibs.bundles.lifecycle)

    implementation(projects.libs.tool.sugar)
    implementation(projects.libs.tool.coroutine)
}