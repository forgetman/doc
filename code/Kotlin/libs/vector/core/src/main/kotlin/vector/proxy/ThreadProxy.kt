@file:Suppress("FunctionName")

package vector.proxy

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import logger.L
import java.lang.reflect.Proxy
import java.util.concurrent.CountDownLatch
import kotlin.reflect.KClass


interface ThreadProxy {
    fun <T : Any> buildProxyFor(kclass: KClass<T>, base: T): T
}

fun <T : Any> T.asThreadProxy(klass: KClass<T>, dispatcher: CoroutineDispatcher): T {
    if (!klass.java.isInterface) throw IllegalArgumentException("T Only support interface")
    return CoroutineThreadProxyImpl(dispatcher).buildProxyFor(klass, this)
}

inline fun <reified T : Any> T.asThreadProxy(dispatcher: CoroutineDispatcher): T {
    return asThreadProxy(T::class, dispatcher)
}

private class CoroutineThreadProxyImpl(private val dispatcher: CoroutineDispatcher) : ThreadProxy {

    @Suppress("OPT_IN_USAGE")
    override fun <T : Any> buildProxyFor(kclass: KClass<T>, base: T): T {
        val javaClass = kclass.java
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            javaClass.classLoader,
            arrayOf(javaClass)
        ) { _, method, args ->
            if (method.returnType == Void.TYPE) {
                GlobalScope.launch(dispatcher) {
                    try {
                        method.invoke(base, *(args ?: emptyArray()))
                    } catch (e: Exception) {
                        L.e(e)
                    }
                }
                return@newProxyInstance null
            } else {
                return@newProxyInstance runBlocking {
                    withContext(dispatcher) {
                        try {
                            method.invoke(base, *(args ?: emptyArray()))
                        } catch (e: Exception) {
                            L.e(e)
                            null
                        }
                    }
                }
            }
        } as T
    }
}

fun <T : Any> T.asThreadProxy(klass: KClass<T>, looper: Looper): T {
    if (!klass.java.isInterface) throw IllegalArgumentException("T Only support interface")
    return HandlerThreadProxyImpl(looper).buildProxyFor(klass, this)
}

inline fun <reified T : Any> T.asThreadProxy(looper: Looper): T {
    return asThreadProxy(T::class, looper)
}

private class HandlerThreadProxyImpl(private val looper: Looper) : ThreadProxy {
    private val handler = Handler(looper)
    override fun <T : Any> buildProxyFor(kclass: KClass<T>, base: T): T {
        val javaClass = kclass.java
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            javaClass.classLoader,
            arrayOf(javaClass)
        ) { _, method, args ->
            if (method.returnType == Void.TYPE) {
                handler.post {
                    try {
                        method.invoke(base, *(args ?: emptyArray()))
                    } catch (e: Exception) {
                        L.e(e)
                    }
                }
                return@newProxyInstance null
            } else {
                // 对于有返回值的方法
                if (Looper.myLooper() == looper) {
                    // 如果当前线程是目标线程，直接调用方法
                    return@newProxyInstance try {
                        method.invoke(base, *(args ?: emptyArray()))
                    } catch (e: Exception) {
                        L.e(e)
                    }
                }

                val latch = CountDownLatch(1)
                val resultHolder = arrayOf<Any?>(null)

                handler.post {
                    try {
                        resultHolder[0] = method.invoke(base, *(args ?: emptyArray()))
                    } catch (e: Exception) {
                        L.e(e)
                    } finally {
                        latch.countDown() // 无论是否成功，释放锁
                    }
                }

                try {
                    latch.await() // 等待主线程执行完成
                } catch (e: InterruptedException) {
                    L.e(e)
                }
                return@newProxyInstance resultHolder[0]
            }
        } as T
    }
}