import convention.ext.addImplementation
import convention.ext.findDependency
import convention.ext.findPluginId
import convention.ext.googleLibs
import com.android.build.api.dsl.ApplicationExtension
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(googleLibs.findPluginId("google-services"))
                apply(googleLibs.findPluginId("firebase-crashlytics"))
            }

            dependencies {
                val bom = googleLibs.findDependency("firebase-bom")
                addImplementation(platform(bom))
                /**
                 * analytics和crashlytics只有在libs.versions.toml里才能正确配置适用于bom的版本
                 * 如果写在自定义的versionCatalog里会报错
                 */
                val analytics = "com.google.firebase:firebase-analytics"
                val crashlytics = "com.google.firebase:firebase-crashlytics"
                "implementation"(analytics)
                "implementation"(crashlytics)
//                addImplementation(analytics)
//                addImplementation(crashlytics)
            }

            extensions.configure<ApplicationExtension> {
                buildTypes.configureEach {
                    // Disable the Crashlytics mapping file upload. This feature should only be
                    // enabled if a Firebase backend is available and configured in
                    // google-services.json.
                    configure<CrashlyticsExtension> {
                        mappingFileUploadEnabled = false
                    }
                }
            }
        }
    }
}
