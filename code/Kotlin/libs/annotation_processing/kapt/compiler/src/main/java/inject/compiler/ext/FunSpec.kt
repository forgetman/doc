package inject.compiler.ext

import com.squareup.kotlinpoet.FunSpec
import inject.compiler.classname.VectorClassName
import inject.compiler.processor.FormatSpecifiers

fun FunSpec.Builder.addEson() {
    addStatement("val eson = ${FormatSpecifiers.TYPE}.default()", VectorClassName.ESON)
}