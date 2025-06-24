package inject.compiler.ext

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import inject.compiler.classname.AndroidClassName
import java.lang.reflect.Type
import javax.lang.model.element.Element

fun createNonNullParam(e: Element, name: String): ParameterSpec {
    val typeName = e.asTypeName()
    val builder = ParameterSpec.builder(name, typeName)
    return builder.build()
}

fun createNullableParam(e: Element, name: String): ParameterSpec {
    val typeName = e.asTypeName().copy(nullable = true)
    val builder = ParameterSpec.builder(name, typeName)
    return builder.build()
}

fun createNonNullParam(className: ClassName, name: String): ParameterSpec {
    val builder = ParameterSpec.builder(name, className)
    return builder.build()
}

fun createNullableParam(className: ClassName, name: String): ParameterSpec {
    val builder = ParameterSpec.builder(name, className.copy(nullable = true))
    return builder.build()
}

fun createNonNullParam(typeName: Type, name: String): ParameterSpec {
    val builder = ParameterSpec.builder(name, typeName)
    return builder.build()
}

fun createNonNullContext(): ParameterSpec {
    return createNonNullParam(AndroidClassName.CONTEXT, "context")
}

fun createNullableContext(): ParameterSpec {
    return createNullableParam(AndroidClassName.CONTEXT, "context")
}