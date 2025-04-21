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
            id = "convention.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "convention.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFlavors") {
            id = "convention.android.application.flavors"
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }
        register("androidFirebase") {
            id = "convention.android.application.firebase"
            implementationClass = "AndroidApplicationFirebaseConventionPlugin"
        }
        register("jvmLibrary") {
            id = "convention.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }

        register("initial") {
            id = "convention.initial"
            implementationClass = "InitialConventionPlugin"
        }

        register("androidxHilt") {
            id = "convention.androidx.hilt"
            implementationClass = "convention.plugin.androidx.HiltConventionPlugin"
        }
        register("androidxRoom") {
            id = "convention.androidx.room"
            implementationClass = "convention.plugin.androidx.RoomConventionPlugin"
        }
        register("androidxMedia3") {
            id = "convention.androidx.media3"
            implementationClass = "convention.plugin.androidx.Media3ConventionPlugin"
        }
        register("androidxComposeApplication") {
            id = "convention.androidx.compose.application"
            implementationClass = "convention.plugin.androidx.ComposeApplicationConventionPlugin"
        }
        register("androidxComposeLibrary") {
            id = "convention.androidx.compose.library"
            implementationClass = "convention.plugin.androidx.ComposeLibraryConventionPlugin"
        }
    }
}