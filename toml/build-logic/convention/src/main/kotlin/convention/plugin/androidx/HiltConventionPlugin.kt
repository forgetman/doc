package convention.plugin.androidx

import convention.ext.addImplementation
import convention.ext.addKsp
import convention.ext.commonLibs
import convention.ext.findDependency
import convention.ext.findPluginId
import convention.ext.googleLibs
import convention.ext.hiltLibs
import convention.ext.kotlinLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(googleLibs.findPluginId("ksp"))
            dependencies {
                addKsp(hiltLibs.findDependency("compiler"))
            }

            pluginManager.withPlugin(kotlinLibs.findPluginId("jvm")) {
                dependencies {
                    addImplementation(hiltLibs.findDependency("core"))
                }
            }

            /** Add support for Android modules, based on [com.android.build.gradle.api.AndroidBasePlugin] */
            pluginManager.withPlugin(commonLibs.findPluginId("android.base")) {
                pluginManager.apply(hiltLibs.findPluginId("dagger"))
                dependencies {
                    addImplementation(hiltLibs.findDependency("android"))
                }
            }
        }
    }
}