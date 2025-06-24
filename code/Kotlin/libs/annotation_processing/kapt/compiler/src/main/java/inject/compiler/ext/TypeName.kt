package inject.compiler.ext

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.BYTE
import com.squareup.kotlinpoet.CHAR
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.SHORT
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import inject.compiler.classname.AndroidClassName
import inject.compiler.classname.JavaClassName

fun TypeName.isPrimitive(): Boolean {
    return this == BOOLEAN
            || this == BOOLEAN.copy(nullable = true)
            || this == JavaClassName.BOXED_BOOLEAN
            || this == BYTE
            || this == BYTE.copy(nullable = true)
            || this == JavaClassName.BOXED_BYTE
            || this == SHORT
            || this == SHORT.copy(nullable = true)
            || this == JavaClassName.BOXED_SHORT
            || this == INT
            || this == INT.copy(nullable = true)
            || this == JavaClassName.BOXED_INT
            || this == LONG
            || this == LONG.copy(nullable = true)
            || this == JavaClassName.BOXED_LONG
            || this == CHAR
            || this == CHAR.copy(nullable = true)
            || this == JavaClassName.BOXED_CHAR
            || this == FLOAT
            || this == FLOAT.copy(nullable = true)
            || this == JavaClassName.BOXED_FLOAT
            || this == DOUBLE
            || this == DOUBLE.copy(nullable = true)
            || this == JavaClassName.BOXED_DOUBLE
}

fun TypeName.isString(): Boolean {
    return this == STRING
            || this == STRING.copy(nullable = true)
            || this == JavaClassName.STRING

}

fun TypeName.isSerializable(): Boolean {
    return this == JavaClassName.SERIALIZABLE
            || this == JavaClassName.SERIALIZABLE.copy(nullable = true)
}

fun TypeName.isParcelable(): Boolean {
    return this == AndroidClassName.PARCELABLE
            || this == AndroidClassName.PARCELABLE.copy(nullable = true)
}

fun TypeName.convertToKotlinType(): TypeName {
    return when (this) {
        JavaClassName.BOXED_INT -> INT
        JavaClassName.BOXED_BOOLEAN -> BOOLEAN
        JavaClassName.BOXED_BYTE -> BYTE
        JavaClassName.BOXED_CHAR -> CHAR
        JavaClassName.BOXED_FLOAT -> FLOAT
        JavaClassName.BOXED_DOUBLE -> DOUBLE
        JavaClassName.BOXED_LONG -> LONG
        JavaClassName.BOXED_SHORT -> SHORT
        JavaClassName.BOXED_VOID -> UNIT
        JavaClassName.STRING -> STRING
        else -> {
            if (this is ParameterizedTypeName) {
                if (this.rawType == JavaClassName.LIST) {
                    val convertArguments = this.typeArguments.map {
                        it.convertToKotlinType()
                    }
                    return LIST.parameterizedBy(convertArguments)
                } else this
            } else this
        }
    }
}