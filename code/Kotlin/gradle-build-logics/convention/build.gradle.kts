import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(commonLibs.android.gradlePlugin)

    compileOnly(kotlinLibs.gradlePlugin)
    compileOnly(kotlinLibs.compose.gradlePlugin)

    compileOnly(googleLibs.ksp.gradlePlugin)
    compileOnly(googleLibs.firebase.crashlytics.gradlePlugin)

    compileOnly(androidxLibs.room.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = conventionLibs.plugins.android.app.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = conventionLibs.plugins.android.lib.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFlavors") {
            id = conventionLibs.plugins.android.flavors.get().pluginId
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }
        register("androidFirebase") {
            id = conventionLibs.plugins.android.firebase.get().pluginId
            implementationClass = "AndroidApplicationFirebaseConventionPlugin"
        }
        register("androidLint") {
            id = conventionLibs.plugins.android.lint.get().pluginId
            implementationClass = "AndroidLintConventionPlugin"
        }

        register("jvmLibrary") {
            id = conventionLibs.plugins.jvmLibrary.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }

        register("initial") {
            id = conventionLibs.plugins.initial.get().pluginId
            implementationClass = "InitialConventionPlugin"
        }

        register("androidxHilt") {
            id = conventionLibs.plugins.androidx.hilt.get().pluginId
            implementationClass = "convention.plugin.androidx.HiltConventionPlugin"
        }
        register("androidxRoom") {
            id = conventionLibs.plugins.androidx.room.get().pluginId
            implementationClass = "convention.plugin.androidx.RoomConventionPlugin"
        }
        register("androidxMedia3") {
            id = conventionLibs.plugins.androidx.media3.get().pluginId
            implementationClass = "convention.plugin.androidx.Media3ConventionPlugin"
        }
        register("androidxCompose") {
            id = conventionLibs.plugins.androidx.compose.get().pluginId
            implementationClass = "convention.plugin.androidx.ComposeConventionPlugin"
        }
    }
}