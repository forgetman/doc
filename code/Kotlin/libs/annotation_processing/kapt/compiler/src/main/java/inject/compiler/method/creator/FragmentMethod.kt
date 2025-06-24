package inject.compiler.method.creator

import com.squareup.kotlinpoet.FunSpec
import inject.compiler.classname.AndroidClassName
import inject.compiler.classname.OtherClassName
import inject.compiler.method.creator.BaseMethod
import inject.compiler.ext.addEson
import inject.compiler.ext.asTypeName
import inject.compiler.ext.getParamName
import inject.compiler.ext.isParcelable
import inject.compiler.ext.isPrimitive
import inject.compiler.ext.isSerializable
import inject.compiler.ext.isString
import inject.compiler.processor.FormatSpecifiers
import inject.compiler.processor.CreatorProcessor
import javax.lang.model.element.Element

/**
 * @author yuansui
 * @since 2020/9/8
 */
internal class FragmentMethod(
    processor: CreatorProcessor,
    annotatedElement: Element
) : BaseMethod<CreatorProcessor>(processor, annotatedElement) {

    fun get(): FunSpec {
        val builder: FunSpec.Builder = FunSpec.builder("get")
            .addStatement("val b = ${FormatSpecifiers.TYPE}()", AndroidClassName.BUNDLE)

        addBundleStatement(builder)

        builder.addStatement("val frag = ${FormatSpecifiers.TYPE}()", annotatedTypeName)
            .addStatement("frag.setArguments(b)")
            .addStatement("return frag")
            .returns(annotatedTypeName)

        return builder.build()
    }

    private fun addBundleStatement(builder: FunSpec.Builder) {
        builder.addEson()
        for (e in allFields) {
            val paramName = e.getParamName()
            val typeName = e.asTypeName()
            if (typeName.isPrimitive() || typeName.isString() || typeName.isSerializable() || typeName.isParcelable()) {
                builder.addStatement(
                    "${FormatSpecifiers.NAME}?.let { b.put${FormatSpecifiers.TYPE}(${FormatSpecifiers.STRING}, it) }",
                    paramName,
                    typeName,
                    paramName
                )
            } else {
                builder.beginControlFlow("try")
                builder.addStatement(
                    "b.putString(${FormatSpecifiers.STRING}, eson.toJson(${FormatSpecifiers.NAME}))",
                    paramName,
                    paramName
                )
                builder.nextControlFlow("catch(e: ${FormatSpecifiers.TYPE})", OtherClassName.EXCEPTION)
                builder.addStatement("e.printStackTrace()")
                builder.endControlFlow()
            }
        }
    }
}
