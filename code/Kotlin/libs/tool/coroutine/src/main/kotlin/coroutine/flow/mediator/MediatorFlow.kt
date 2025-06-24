package coroutine.flow.mediator

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Suppress("FunctionName")
inline fun <reified T> MediatorFlow(
    vararg flows: StateFlow<T>,
    crossinline operation: (accumulator: T, value: T) -> T
): Flow<T> {
    return MediatorFlow(flows.toList(), operation)
}

/**
 * 用于多个Flow的值进行合并
 *
 * @param flows 需要合并的Flow
 * @param operation 合并操作, 其中accumulator是累加的值, value是当前的值
 */
@Suppress("FunctionName")
inline fun <reified T> MediatorFlow(
    flows: List<Flow<T>>,
    crossinline operation: (accumulator: T, value: T) -> T
): Flow<T> {
    return kotlinx.coroutines.flow.combine(flows) { values ->
        values.reduce { accumulator, value ->
            operation(accumulator, value)
        }
    }
}