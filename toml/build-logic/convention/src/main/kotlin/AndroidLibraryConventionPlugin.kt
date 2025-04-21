import com.android.build.gradle.LibraryExtension
import convention.ext.commonLibs
import convention.ext.configure
import convention.ext.configureFlavors
import convention.ext.configureKotlinAndroid
import convention.ext.findIntVersion
import convention.ext.findPluginId
import convention.ext.kotlinLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import kotlin.collections.distinct
import kotlin.collections.drop
import kotlin.collections.joinToString
import kotlin.text.lowercase
import kotlin.text.split
import kotlin.text.toRegex

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            repositories.configure(this)

            with(pluginManager) {
                apply(commonLibs.findPluginId("android.lib"))
                apply(kotlinLibs.findPluginId("android"))
            }

            pluginManager.withPlugin(kotlinLibs.findPluginId("kapt")) {
                configure<KaptExtension> { useBuildCache = true }
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = commonLibs.findIntVersion("targetSdk")
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testOptions.animationsDisabled = true
                configureFlavors(this)
//                configureGradleManagedDevices(this)
                // The resource prefix is derived from the module name,
                // so resources inside ":core:module1" must be prefixed with "core_module1_"
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
            }
//            extensions.configure<LibraryAndroidComponentsExtension> {
//                configurePrintApksTask(this)
//                disableUnnecessaryAndroidTests(target)
//            }
//            dependencies {
//                add("androidTestImplementation", kotlin("test"))
//                add("testImplementation", kotlin("test"))
//
//                add("implementation", libs.findLibrary("androidx.tracing.ktx").get())
//            }
        }
    }
}
