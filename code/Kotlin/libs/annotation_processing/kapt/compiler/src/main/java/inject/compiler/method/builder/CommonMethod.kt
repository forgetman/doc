package inject.compiler.method.builder

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import inject.compiler.ext.asTypeName
import inject.compiler.processor.BuilderProcessor
import javax.lang.model.element.Element

/**
 * @author yuansui
 * @since 2024/12/6
 */
internal class CommonMethod(
    private val processor: BuilderProcessor,
    private val annotatedElement: Element,
    private val allFields: List<Element>
) {

    fun assignMethods(fileName: String): List<FunSpec> {
        return allFields.map { e ->
            val paramName = e.simpleName.toString()
            FunSpec.builder(paramName)
                .addParameter(paramName, e.asTypeName().copy(nullable = true))
                .addStatement("this.$paramName = $paramName")
                .addStatement("return this")
                .returns(ClassName(processor.getPackageName(annotatedElement), fileName))
                .build()
        }
    }

    fun buildMethod(): FunSpec {
        val clzName = annotatedElement.asTypeName()
        val builder = FunSpec.builder("build").returns(clzName)

        val constructorStringBuilder = StringBuilder()
        constructorStringBuilder.append("return $clzName(")
        allFields.forEach { e ->

            val paramName = e.simpleName.toString()
            builder.addStatement("val $paramName = requireNotNull($paramName) {\"$paramName is required\"}")
            constructorStringBuilder.append("$paramName, ")
        }
        constructorStringBuilder.append(")")
        builder.addStatement(constructorStringBuilder.toString())

        return builder.build()
    }
}