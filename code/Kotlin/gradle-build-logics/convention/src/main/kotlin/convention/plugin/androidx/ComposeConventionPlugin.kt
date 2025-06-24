package convention.plugin.androidx

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import convention.ext.commonLibs
import convention.ext.configureAndroidCompose
import convention.ext.findPluginId
import convention.ext.kotlinLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(kotlinLibs.findPluginId("compose"))

            when {
                pluginManager.hasPlugin(commonLibs.findPluginId("android.app")) -> {
                    val extension = extensions.getByType<ApplicationExtension>()
                    configureAndroidCompose(extension)
                }

                pluginManager.hasPlugin(commonLibs.findPluginId("android.lib")) -> {
                    val extension = extensions.getByType<LibraryExtension>()
                    configureAndroidCompose(extension)
                }
            }
        }
    }
}