package eth.internal

import eth.ext.paramUpperBound
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type

/**
 * @author yuansui
 * @since 2019/10/31
 */
internal class FlowCallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type): CallAdapter<*, *>? {
        if (getRawType(returnType) != Flow::class.java) return null

        val responseType = returnType.paramUpperBound
        return FlowCallAdapter<Any>(responseType)
    }
}
