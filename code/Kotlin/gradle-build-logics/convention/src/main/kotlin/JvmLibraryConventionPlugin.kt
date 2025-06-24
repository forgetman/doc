import convention.ext.configureKotlinJvm
import convention.ext.findPluginId
import convention.ext.kotlinLibs
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(kotlinLibs.findPluginId("jvm"))
            }
            configureKotlinJvm()
        }
    }
}