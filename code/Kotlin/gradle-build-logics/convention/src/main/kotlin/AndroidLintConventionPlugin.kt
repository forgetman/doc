import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import convention.ext.commonLibs
import convention.ext.findPluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidLintConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin(commonLibs.findPluginId("android.app")) -> {
                    configure<ApplicationExtension> { lint(Lint::configure) }
                }

                pluginManager.hasPlugin(commonLibs.findPluginId("android.lib")) -> {
                    configure<LibraryExtension> { lint(Lint::configure) }
                }

                else -> {
                    apply(plugin = commonLibs.findPluginId("android.lint"))
                    configure<Lint>(Lint::configure)
                }
            }
        }
    }
}

private fun Lint.configure() {
    xmlReport = true
    sarifReport = true
    checkDependencies = true
    disable += listOf("GradleDependency", "ResourceName")
}