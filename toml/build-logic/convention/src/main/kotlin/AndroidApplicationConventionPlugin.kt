import com.android.build.api.dsl.ApplicationExtension
import convention.ext.commonLibs
import convention.ext.configure
import convention.ext.configureKotlinAndroid
import convention.ext.findIntVersion
import convention.ext.findPluginId
import convention.ext.kotlinLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            repositories.configure(this)

            with(pluginManager) {
                apply(commonLibs.findPluginId("android.app"))
                apply(kotlinLibs.findPluginId("android"))
            }

            pluginManager.withPlugin("kotlin-kapt") {
                configure<KaptExtension> { useBuildCache = true }
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = commonLibs.findIntVersion("targetSdk")
                defaultConfig.multiDexEnabled = true
                @Suppress("UnstableApiUsage")
                testOptions.animationsDisabled = true
            }
        }
    }

}
