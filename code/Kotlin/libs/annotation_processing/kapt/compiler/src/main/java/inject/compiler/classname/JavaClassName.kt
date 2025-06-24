package inject.compiler.classname

import com.squareup.kotlinpoet.ClassName

/**
 * @author yuansui
 * @since 2024/8/22
 */
object JavaClassName {
    val SERIALIZABLE = ClassName("java.io", "Serializable")
    val STRING = ClassName("java.lang", "String")
    val LIST = ClassName("java.util", "List")

    val BOXED_VOID = ClassName("java.lang", "Void")
    val BOXED_BOOLEAN = ClassName("java.lang", "Boolean")
    val BOXED_BYTE = ClassName("java.lang", "Byte")
    val BOXED_SHORT = ClassName("java.lang", "Short")
    val BOXED_INT = ClassName("java.lang", "Integer")
    val BOXED_LONG = ClassName("java.lang", "Long")
    val BOXED_CHAR = ClassName("java.lang", "Character")
    val BOXED_FLOAT = ClassName("java.lang", "Float")
    val BOXED_DOUBLE = ClassName("java.lang", "Double")
}