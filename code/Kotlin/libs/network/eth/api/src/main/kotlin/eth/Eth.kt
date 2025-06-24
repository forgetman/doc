package eth

import com.google.gson.TypeAdapterFactory
import eson.Eson
import eth.api.service.HttpService
import eth.convertor.Converter
import eth.ext.paramUpperBound
import eth.interceptor.PreInterceptor
import eth.internal.CallAdapter
import eth.internal.FlowCallAdapterFactory
import eth.internal.PreInterceptorChain
import eth.internal.ServiceMethod
import sugar.ext.doOnNotNull
import sugar.ext.self
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

/**
 * http网络库
 * @since : 2019/5/21
 */
class Eth private constructor() {

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }

    private lateinit var networkService: HttpService

    private var baseUrl: String? = null
    private var interceptors: List<PreInterceptor>? = null
    private var converters: List<Converter>? = null
    private var callAdapterFactories: List<CallAdapter.Factory>? = null
    private var typeFactories: List<TypeAdapterFactory>? = null

    private lateinit var eson: Eson

    fun <T : Any> create(service: KClass<T>): T {
        return create(service.java)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(service: Class<T>): T {
        return Proxy.newProxyInstance(service.classLoader, arrayOf(service)) { _, method, args ->
            val request = ServiceMethod.method(baseUrl, method, args)

            // Interceptor 拦截参数
            val newRequest = interceptors?.let {
                val chain = PreInterceptorChain(it, 0, request)
                chain.proceed(request)
            } ?: request

            // create Task with Request
            val task: Task<T> = networkService.createTask(newRequest)
            task.converters = converters
            task.eson = eson

            // find adapter
            val returnType = request.returnType
            val adapter = doOnNotNull(returnType, callAdapterFactories) { type, factories ->
                factories.forEach {
                    val a = it.get(type) ?: return@forEach
                    if (a.responseType() == type.paramUpperBound) {
                        return@doOnNotNull a
                    }
                }
                null
            } ?: throw NullPointerException("找不到response对应的adapter: $returnType")

            adapter as CallAdapter<T, *>
            adapter.adapt(task)
        } as T
    }

    fun newBuilder(): Builder {
        val builder = builder()
        builder.baseUrl(baseUrl)
        converters?.forEach {
            builder.addConverter(it)
        }
        interceptors?.forEach {
            builder.addInterceptor(it)
        }
        callAdapterFactories?.forEach {
            builder.addCallAdapterFactory(it)
        }
        typeFactories?.forEach {
            builder.addTypeAdapterFactory(it)
        }
        builder.service(networkService.newBuilder().build())

        return builder
    }

    class Builder internal constructor() {

        private val interceptors =
            lazy(LazyThreadSafetyMode.NONE) { mutableListOf<PreInterceptor>() }

        private val converters =
            lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Converter>() }

        private val typeFactories =
            lazy(LazyThreadSafetyMode.NONE) { mutableListOf<TypeAdapterFactory>() }

        private val callAdapterFactories = mutableListOf<CallAdapter.Factory>()

        private var baseUrl: String? = null

        private var httpService: HttpService? = null

        fun baseUrl(url: String?) = self { baseUrl = url }

        fun service(serv: HttpService) = self { httpService = serv }

        fun addInterceptor(interceptor: PreInterceptor) = self { interceptors.value.add(interceptor) }

        fun addConverter(converter: Converter) = self { converters.value.add(converter) }

        fun addTypeAdapterFactory(factory: TypeAdapterFactory) =
            self { typeFactories.value.add(factory) }

        fun addCallAdapterFactory(factory: CallAdapter.Factory) =
            self { callAdapterFactories.add(factory) }

        fun build(): Eth {
            val e = Eth()
            e.baseUrl = baseUrl

            if (converters.isInitialized()) {
                e.converters = converters.value
            }

            if (interceptors.isInitialized()) {
                e.interceptors = interceptors.value
            }

            // 默认添加协程的支持
            callAdapterFactories.add(FlowCallAdapterFactory())
            e.callAdapterFactories = callAdapterFactories

            // 处理factory
            e.eson = if (typeFactories.isInitialized()) {
                e.typeFactories = typeFactories.value
                Eson.create(typeFactories.value)
            } else {
                Eson.default()
            }

            httpService?.let {
                e.networkService = it
            } ?: throw NullPointerException("HttpService不能为空")

            return e
        }
    }
}