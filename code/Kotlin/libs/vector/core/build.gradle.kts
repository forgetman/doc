plugins {
    alias(conventionLibs.plugins.android.lib)
    alias(conventionLibs.plugins.android.lint)
    alias(kotlinLibs.plugins.kapt)
}

android {
    namespace = "vector"

    defaultConfig {
        consumerProguardFiles(
            "proguard-vector.pro",
            rootProject.file("libs/annotation_processing/kapt/compiler/proguard-injection.pro")
        )
    }

    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/kotlin-app",
                "src/main/kotlin-compat",
                "src/main/kotlin-widget"
            )
        }
    }
}

dependencies {
    /**
     * base
     */
    api(projects.libs.tool.live)
    api(projects.libs.tool.sugar)
    api(projects.libs.tool.coroutine)
    api(projects.libs.tool.compat)
    api(projects.libs.network.eth.okhttp)
    api(projects.libs.eson)
    api(projects.libs.annotationProcessing.kapt.annotation)
    kapt(projects.libs.annotationProcessing.kapt.compiler)


    /**
     * androidx
     */
    compileOnly(androidxLibs.annotation)
    api(androidxLibs.core.splashscreen)
    api(androidxLibs.appcompat)
    api(androidxLibs.fragment)
    api(androidxLibs.constraintLayout)
    api(androidxLibs.recyclerview)
    api(androidxLibs.dex)
    api(androidxLibs.gridlayout)
    api(androidxLibs.exifinterface)
    api(androidxLibs.vectorDrawable.runtime)
    api(androidxLibs.vectorDrawable.animated)
    api(androidxLibs.webkit)
    implementation(androidxLibs.viewpager2)
    api(androidxLibs.datastore.preferences)
    api(androidxLibs.datastore.preferences.proto)
//    implementation(androidxLibs.datastore.proto)
    api(androidxLibs.bundles.lifecycle)

    /**
     * google
     */
    api(googleLibs.material)

    implementation(squareLibs.okio)
}