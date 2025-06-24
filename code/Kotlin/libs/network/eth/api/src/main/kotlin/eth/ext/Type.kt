package eth.ext

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

internal val Type.paramUpperBound: Type
    get() {
        return if (this is ParameterizedType) {
            val types = this.actualTypeArguments
            val paramType = types[0]
            if (paramType is WildcardType) {
                paramType.upperBounds[0]
            } else {
                paramType
            }
        } else {
            this
        }
    }