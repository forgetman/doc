@file:Suppress("OPT_IN_USAGE")

package vector.ext

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sugar.collection.SafeMutableList
import kotlin.coroutines.CoroutineContext

fun <T> SafeMutableList<T>.dispatchEach(context: CoroutineContext, action: (T) -> Unit) {
    GlobalScope.launch(context) {
        forEachElement(action)
    }
}

fun <E> List<E>.dispatchEach(context: CoroutineContext, action: (E) -> Unit) {
    GlobalScope.launch(context) {
        forEach(action)
    }
}