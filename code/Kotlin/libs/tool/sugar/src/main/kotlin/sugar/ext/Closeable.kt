package sugar.ext

import logger.L
import java.io.Closeable
import java.io.IOException
import java.io.OutputStream
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * 安全关闭
 */
fun Closeable?.safeClose(cause: Throwable? = null) {
    this ?: return

    try {
        if (this is OutputStream) this.flush()
        close()
    } catch (e: IOException) {
        if (cause != null) {
            cause.addSuppressed(e)
            L.e(cause)
        } else {
            L.e(e)
        }
    }
}

/**
 * 安全使用
 */
@OptIn(ExperimentalContracts::class)
inline fun <T : Closeable?, R> T.safeUse(block: (T) -> R): R? {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    var exception: Throwable? = null
    return try {
        block(this)
    } catch (e: Throwable) {
        exception = e
        L.e(e)
        null
    } finally {
        when {
            this == null -> {}
            exception == null -> close()
            else -> safeClose(exception)
        }
    }
}

/**
 * 安全关闭
 */
fun AutoCloseable?.safeClose(cause: Throwable? = null) {
    this ?: return
    try {
        close()
    } catch (e: IOException) {
        if (cause != null) {
            cause.addSuppressed(e)
            L.e(cause)
        } else {
            L.e(e)
        }
    }
}

/**
 * 安全使用
 */
@OptIn(ExperimentalContracts::class)
inline fun <T : AutoCloseable?, R> T.safeUse(block: (T) -> R): R? {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    var exception: Throwable? = null
    return try {
        block(this)
    } catch (e: Throwable) {
        exception = e
        L.e(e)
        null
    } finally {
        when {
            this == null -> {}
            exception == null -> close()
            else -> safeClose(exception)
        }
    }
}