package convention.plugin.androidx

import com.android.build.api.dsl.ApplicationExtension
import convention.ext.commonLibs
import convention.ext.configureAndroidCompose
import convention.ext.findPluginId
import convention.ext.kotlinLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class ComposeApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(commonLibs.findPluginId("android.app"))
            pluginManager.apply(kotlinLibs.findPluginId("compose"))

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }

}