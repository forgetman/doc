package convention.plugin.androidx

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import convention.ext.addImplementation
import convention.ext.androidxLibs
import convention.ext.findDependency
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class Media3ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    configure<ApplicationExtension> {
                        lint {
                            mergeLintConfig(this)
                        }
                    }

                pluginManager.hasPlugin("com.android.library") ->
                    configure<LibraryExtension> {
                        lint {
                            mergeLintConfig(this)
                        }
                    }

                else -> {
                    pluginManager.apply("com.android.lint")
                    configure<Lint> {
                        mergeLintConfig(this)
                    }
                }
            }

            dependencies {
                addImplementation(androidxLibs.findDependency("media3.session"))
                addImplementation(androidxLibs.findDependency("media3.player"))
            }
        }
    }

    private fun mergeLintConfig(lint: Lint) {
        lint.xmlReport = true
        lint.checkDependencies = true

        // 合并 lintConfig 文件内容
        val existingLintConfig = lint.lintConfig
        val newLintConfigFile = File.createTempFile("lint", ".xml").apply {
            deleteOnExit()
        }
        val newLintConfigContent = generateLintXmlContent()
        if (existingLintConfig != null && existingLintConfig.exists()) {
            val existingContent = existingLintConfig.readText()
            val mergedContent = mergeLintXmlContents(existingContent, newLintConfigContent)
            newLintConfigFile.writeText(mergedContent)
        } else {
            newLintConfigFile.writeText(newLintConfigContent)
        }
        lint.lintConfig = newLintConfigFile
    }

    private fun mergeLintXmlContents(existing: String, new: String): String {
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

        // 解析现有 XML 文档
        val existingDoc = docBuilder.parse(existing.byteInputStream())
        existingDoc.documentElement.normalize()

        // 解析新的 XML 文档
        val newDoc = docBuilder.parse(new.byteInputStream())
        newDoc.documentElement.normalize()

        // 获取现有文档的 <lint> 根节点
        val existingLintElement = existingDoc.documentElement

        // 获取新文档的 <issue> 节点列表
        val newIssueNodes = newDoc.getElementsByTagName("issue")

        // 合并 <issue> 节点
        for (i in 0 until newIssueNodes.length) {
            val newIssueNode = newIssueNodes.item(i) as Element
            val existingIssueNode = findExistingIssueNode(existingLintElement, newIssueNode)
            if (existingIssueNode == null) {
                // 不存在相同 id 的 <issue> 节点，直接添加
                val importedNode = existingDoc.importNode(newIssueNode, true)
                existingLintElement.appendChild(importedNode)
            } else {
                // 存在相同 id 的 <issue> 节点，合并 <option> 节点
                mergeOptions(existingIssueNode, newIssueNode, existingDoc)
            }
        }

        // 将合并后的文档转换为字符串
        return documentToString(existingDoc)
    }

    private fun findExistingIssueNode(lintElement: Element, newIssueNode: Element): Element? {
        val newIssueId = newIssueNode.getAttribute("id")
        val existingIssueNodes = lintElement.getElementsByTagName("issue")
        for (i in 0 until existingIssueNodes.length) {
            val existingIssueNode = existingIssueNodes.item(i) as Element
            if (existingIssueNode.getAttribute("id") == newIssueId) {
                return existingIssueNode
            }
        }
        return null
    }

    private fun mergeOptions(existingIssueNode: Element, newIssueNode: Element, doc: Document) {
        val newOptionNodes = newIssueNode.getElementsByTagName("option")
        for (i in 0 until newOptionNodes.length) {
            val newOptionNode = newOptionNodes.item(i) as Element
            val newOptionName = newOptionNode.getAttribute("name")
            val existingOptionNodes = existingIssueNode.getElementsByTagName("option")
            var optionExists = false
            for (j in 0 until existingOptionNodes.length) {
                val existingOptionNode = existingOptionNodes.item(j) as Element
                if (existingOptionNode.getAttribute("name") == newOptionName) {
                    // 存在相同 name 的 <option> 节点，覆盖其 value
                    existingOptionNode.setAttribute("value", newOptionNode.getAttribute("value"))
                    optionExists = true
                    break
                }
            }
            if (!optionExists) {
                // 不存在相同 name 的 <option> 节点，添加新的 <option> 节点
                val importedOptionNode = doc.importNode(newOptionNode, true)
                existingIssueNode.appendChild(importedOptionNode)
            }
        }
    }

    private fun documentToString(doc: Document): String {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val sw = java.io.StringWriter()
        val result = StreamResult(sw)
        val source = DOMSource(doc)
        transformer.transform(source, result)
        return sw.toString()
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
}