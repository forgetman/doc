package eth.internal

import eth.Task
import java.lang.reflect.*
import java.lang.reflect.Array

/**
 * @author yuansui
 * @since 2019/10/31
 */
interface CallAdapter<R, T> {
    fun responseType(): Type
    fun adapt(task: Task<R>): T

    abstract class Factory {
        abstract fun get(returnType: Type): CallAdapter<*, *>?

        companion object {
            /**
             * from retrofit
             */
            internal fun getRawType(type: Type): Class<*> {
                if (type is Class<*>) {
                    // Type is a normal class.
                    return type
                }
                if (type is ParameterizedType) {

                    // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
                    // suspects some pathological case related to nested classes exists.
                    val rawType = type.rawType
                    require(rawType is Class<*>)
                    return rawType
                }
                if (type is GenericArrayType) {
                    val componentType = type.genericComponentType
                    return Array.newInstance(getRawType(componentType), 0).javaClass
                }
                if (type is TypeVariable<*>) {
                    // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
                    // type that's more general than necessary is okay.
                    return Any::class.java
                }
                if (type is WildcardType) {
                    return getRawType(type.upperBounds[0])
                }

                throw IllegalArgumentException(
                    "Expected a Class, ParameterizedType, or "
                            + "GenericArrayType, but <" + type + "> is of type " + type.javaClass.name
                )
            }

        }
    }
}

