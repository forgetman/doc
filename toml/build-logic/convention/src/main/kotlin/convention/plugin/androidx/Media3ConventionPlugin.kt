package convention.plugin.androidx

import convention.ext.addImplementation
import convention.ext.androidxLibs
import convention.ext.findDependency
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import java.io.File

class Media3ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    configure<ApplicationExtension> { lint(Lint::configure) }

                pluginManager.hasPlugin("com.android.library") ->
                    configure<LibraryExtension> { lint(Lint::configure) }

                else -> {
                    pluginManager.apply("com.android.lint")
                    configure<Lint>(Lint::configure)
                }
            }

            dependencies {
                addImplementation(androidxLibs.findDependency("media3.session"))
                addImplementation(androidxLibs.findDependency("media3.player"))
            }
        }
    }
}

private fun Lint.configure() {
    xmlReport = true
    checkDependencies = true

    // 设置 lintConfig 文件
    configureLintConfigFile()
}

private fun Lint.configureLintConfigFile() {
    // 创建一个临时文件来存储 Lint 配置
    val lintConfigFile = File.createTempFile("lint", ".xml").apply {
        deleteOnExit() // 确保 JVM 退出时删除临时文件
    }

    // 写入 Lint 配置内容
    lintConfigFile.writeText(generateLintXmlContent())

    // 设置 lintConfig 属性
    this.lintConfig = lintConfigFile
}

private fun generateLintXmlContent(): String {
    return """
        <lint>
            <issue id="UnsafeOptInUsageError">
                <option name="opt-in" value="androidx.media3.common.util.UnstableApi" />
            </issue>
        </lint>
    """.trimIndent()
}