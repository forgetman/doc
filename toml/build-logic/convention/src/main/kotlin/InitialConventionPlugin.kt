import convention.ext.configure
import org.gradle.api.Plugin
import org.gradle.api.Project

class InitialConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            repositories.configure(this)

            allprojects {
                val path = "gradle-build-logics/tomls"
                fileTree(path).forEach { file ->
                    val tomlContent = file.readText()
                    val parsedData = parseTomlSectionsWithDetailedModules(tomlContent)
                    val modules = parsedData.libraries.filter { (_, value) ->
                        value.version.isNotEmpty()
                    }.values.map { it.module }

                    subprojects {
                        project.configurations.configureEach {
                            modules.forEach { value ->
                                resolutionStrategy.force(value)
                            }
                        }
                    }
                }
            }
        }
    }

    // 解析 TOML 文件内容，包含详细的 group 和 name 提取
    fun parseTomlSectionsWithDetailedModules(tomlContent: String): ParsedTomlData {
        val versions = mutableMapOf<String, String>()
        val libraries = mutableMapOf<String, Libraries>()
        val plugins = mutableMapOf<String, String>()

        var currentSection = ""
        tomlContent.lineSequence().forEach { line ->
            val trimmedLine = line.trim()

            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith(";")) return@forEach

            if (trimmedLine.startsWith("[") && trimmedLine.endsWith("]")) {
                currentSection = trimmedLine.removeSurrounding("[", "]").trim()
            } else {
                when (currentSection) {
                    "versions" -> {
                        val (key, value) = parseTomlKeyValue(trimmedLine)
                        versions[key] = value.removeSurrounding("\"")
                            .removeSurrounding("\'") // 去除引号
                    }

                    "libraries" -> {
                        val (key, value) = parseTomlKeyValue(trimmedLine)
                        val data = parseLibraryDetailsWithGroupAndName(value, versions)
                        val group = data["group"] ?: ""
                        val name = data["name"] ?: ""
                        val version = data["version"] ?: ""
                        val module = "$group:$name:$version"
                        libraries[key] = Libraries(group, name, version, module)
                    }

                    "plugins" -> {
                        val (key, value) = parseTomlKeyValue(trimmedLine)
                        plugins[key] = value
                    }
                }
            }
        }
        return ParsedTomlData(versions, libraries, plugins)
    }

    // 解析 library 的详细信息，将 module 拆解为 group 和 name，并解析 version.ref
    fun parseLibraryDetailsWithGroupAndName(
        value: String,
        versions: Map<String, String>
    ): Map<String, String> {
        val details = mutableMapOf<String, String>()

        // 使用正则表达式提取字段
        val groupRegex = """group\s*=\s*["']([^"']+)["']""".toRegex()
        val nameRegex = """name\s*=\s*["']([^"']+)["']""".toRegex()
        val moduleRegex = """module\s*=\s*["']([^"']+):([^"']+)["']""".toRegex()
        val versionRegex = """version(\.ref)?\s*=\s*["']([^"']+)["']""".toRegex()

        // 提取 group 和 name
        groupRegex.find(value)?.let { matchResult ->
            details["group"] = matchResult.groupValues[1]
        }
        nameRegex.find(value)?.let { matchResult ->
            details["name"] = matchResult.groupValues[1]
        }

        // 如果没有单独的 group 和 name，尝试从 module 中提取
        if (!details.containsKey("group") && !details.containsKey("name")) {
            moduleRegex.find(value)?.let { matchResult ->
                details["group"] = matchResult.groupValues[1]
                details["name"] = matchResult.groupValues[2]
            }
        }

        // 提取并替换 version.ref 引用
        versionRegex.find(value)?.let { matchResult ->
            val versionKey = matchResult.groupValues[2]
            details["version"] = versions[versionKey] ?: versionKey // 找不到则保留原始值
        }

        return details
    }

    // 解析 TOML 文件中的键值对
    fun parseTomlKeyValue(line: String): Pair<String, String> {
        val parts = line.split("=", limit = 2)
        val key = parts[0].trim()
        val value = parts.getOrElse(1) { "" }.trim()
        return key to value
    }

    // 封装解析结果的类
    data class ParsedTomlData(
        val versions: Map<String, String>,
        val libraries: Map<String, Libraries>,
        val plugins: Map<String, String>
    )

    data class Libraries(
        val group: String,
        val name: String,
        val version: String,
        val module: String
    )
}