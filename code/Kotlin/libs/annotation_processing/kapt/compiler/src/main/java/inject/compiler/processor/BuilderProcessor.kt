package inject.compiler.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import inject.annotation.builder.Builder
import inject.compiler.ext.asTypeName
import inject.compiler.method.builder.CommonMethod
import javax.annotation.processing.Processor
import javax.lang.model.element.Element

/**
 * 用来生成简易的builder模式, 如果需要复杂的判断或者重载更多的方法, 需要自己写builder
 *
 * @author yuansui
 * @since 2017/8/2
 */
@AutoService(Processor::class)
class BuilderProcessor : BaseProcessor() {

    override fun getAnnotationClass(): Class<out Annotation> {
        return Builder::class.java
    }

    override fun createTypeSpec(annotatedElement: Element): TypeSpec {
        val fileName = String.format("%sBuilder", annotatedElement.simpleName)
        val builder = TypeSpec.Companion.classBuilder(fileName)
            .primaryConstructor(
                FunSpec.Companion.constructorBuilder().build()
            )

        val allFields = annotatedElement.enclosedElements.filter { e -> e.kind.isField }
        allFields.forEach { e ->
            println("e: ${e.simpleName}")
            val paramName = e.simpleName.toString()
            // 根据类型生成对应的成员变量var xxx
            val type = e.asTypeName()
            builder.addProperty(
                PropertySpec.builder(paramName, type.copy(nullable = true))
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("null")
                    .mutable()
                    .build()
            )
        }

        val commonMethod = CommonMethod(this, annotatedElement, allFields)
        builder.addFunctions(commonMethod.assignMethods(fileName))
        builder.addFunction(commonMethod.buildMethod())

        return builder.build()
    }
}