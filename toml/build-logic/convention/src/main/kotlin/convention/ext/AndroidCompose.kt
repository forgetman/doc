package convention.ext

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            addImplementation(androidxComposeLibs.findDependency("runtime"))
            addImplementation(androidxComposeLibs.findDependency("ui"))
            addImplementation(androidxComposeLibs.findDependency("ui.tooling.preview"))
            addImplementation(androidxComposeLibs.findDependency("material3"))

            addImplementation(androidxLibs.findDependency("activity.compose"))
            addImplementation(androidxLibs.findDependency("fragment.compose"))

            addDebugImplementation(androidxComposeLibs.findDependency("ui.tooling"))
        }

        @Suppress("UnstableApiUsage")
        testOptions {
            unitTests {
                // For Robolectric
                isIncludeAndroidResources = true
            }
        }

        composeOptions {
            kotlinCompilerExtensionVersion = androidxComposeLibs.findStringVersion("compiler")
        }
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }
        fun Provider<*>.relativeToRootProject(dir: String) = map {
            @Suppress("UnstableApiUsage")
            isolated.rootProject.projectDirectory
                .dir("build")
                .dir(projectDir.toRelativeString(rootDir))
        }.map { it.dir(dir) }

        project.providers.gradleProperty("enableComposeCompilerMetrics").onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        project.providers.gradleProperty("enableComposeCompilerReports").onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        // 推荐使用module的compose_compiler_config.conf, 不使用rootProject的
        stabilityConfigurationFiles
            .add(project.layout.projectDirectory.file("compose_compiler_config.conf"))
//            .add(isolated.rootProject.projectDirectory.file("compose_compiler_config.conf"))
    }
}
