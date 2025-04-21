package convention.plugin.androidx

import androidx.room.gradle.RoomExtension
import com.google.devtools.ksp.gradle.KspExtension
import convention.ext.addImplementation
import convention.ext.addKsp
import convention.ext.androidxLibs
import convention.ext.findDependency
import convention.ext.findPluginId
import convention.ext.googleLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class RoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(androidxLibs.findPluginId("room"))
            pluginManager.apply(googleLibs.findPluginId("ksp"))

            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
            }

            extensions.configure<RoomExtension> {
                // The schemas directory contains a schema file for each version of the Room database.
                // This is required to enable Room auto migrations.
                // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                addImplementation(androidxLibs.findDependency("room.runtime"))
                addImplementation(androidxLibs.findDependency("room.ktx"))
                addKsp(androidxLibs.findDependency("room.compiler"))
            }
        }
    }
}