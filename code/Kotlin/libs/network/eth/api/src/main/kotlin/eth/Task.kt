package eth

import eson.Eson
import eth.convertor.Converter
import eth.def.AsyncResult
import eth.def.Result
import eth.def.SyncResult
import eth.model.ErrorDefaultCode
import eth.model.EthException
import eth.model.Progress
import eth.model.Response

abstract class Task<T>(protected val request: eth.model.Request) {

    fun interface ProgressListener {
        fun onProgress(progress: Progress)
    }

    var converters: List<Converter>? = null
    lateinit var eson: Eson
    var progressListener: ProgressListener? = null

    /**
     * 解析数据
     */
    protected fun onResponse(response: Response, result: Result<T>) {
        val converters = converters
        if (converters == null) {
            // 没有解析器
            result.isSuccessful = false
            result.exception = EthException(ErrorDefaultCode.EMPTY_CONVERTER, "converter为空, 无法解析数据")
        } else {
            for (index in converters.indices) {
                val converter = converters[index]
                try {
                    val data = converter.onResponse<T>(response, eson)
                    if (data == null) {
                        // 解析失败
                        if (index == converters.lastIndex) {
                            // 没有其他解析器了
                            result.isSuccessful = false
                            result.exception = EthException(
                                ErrorDefaultCode.NON_HANDLED_CONVERTER,
                                "没有可以处理当前数据的converter"
                            )
                            break
                        }
                        continue
                    } else {
                        // 解析成功
                        result.isSuccessful = true
                        result.data = data
                        break
                    }
                } catch (t: Throwable) {
                    // 如果有异常抛出, 则中止converter的遍历, 认为已经有正确的converter处理了数据
                    result.isSuccessful = false
                    result.exception = t
                    break
                }
            }
        }
    }

    abstract fun cancel()

    /**
     * 同步执行
     */
    abstract fun execute(): SyncResult<T>

    /**
     * 异步执行
     */
    abstract fun enqueue(): AsyncResult<T>
}