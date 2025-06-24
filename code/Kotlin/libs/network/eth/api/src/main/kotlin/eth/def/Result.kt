package eth.def

internal typealias OnResultFailure = (Throwable) -> Unit
internal typealias OnResultSuccess<T> = (T) -> Unit

/**
 * @author yuansui
 * @since 2020/9/22
 */
open class Result<T> {
    var code: Int = -1
    var isSuccessful: Boolean = false
    var exception: Throwable = IllegalStateException("exception is null")
    var data: T? = null
}

class SyncResult<T> : Result<T>()

class AsyncResult<T> : Result<T>() {
    var onFailure: OnResultFailure? = null
    var onSuccess: OnResultSuccess<T>? = null
}