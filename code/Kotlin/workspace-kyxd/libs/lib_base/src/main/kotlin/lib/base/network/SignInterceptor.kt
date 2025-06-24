package lib.base.network

import eth.interceptor.PreInterceptor
import eth.model.Request
import sugar.ext.isNotNull
import vector.ext.md5

/**
 * @author yuansui
 * @since 2019-07-11
 */
class SignInterceptor : PreInterceptor {

    companion object {
        private const val SIGN = "sign"
        private const val KEY = "key"
        private const val KEY_VALUE = "ouJmPL7e0g1fLWhwQs"

        internal const val SYMBOL_AND = '&'
        internal const val SYMBOL_EQUAL = '='
    }

    override fun intercept(chain: PreInterceptor.Chain): Request {
        val oldRequest = chain.request()
        val request = oldRequest.newBuilder()
            .apply {
                val list = mutableListOf<Pair<String, Any>>()

                val all = oldRequest.params?.toList()

                /**
                 * 1. 去掉空值
                 */
                all?.filter {
                    it.second.isNotNull()
                }?.forEach {
                    list += it
                }

                /**
                 * 2. 排序
                 */
                list.sortWith(Comparator { o1, o2 ->
                    o1.first.compareTo(o2.first, ignoreCase = true)
                })
                list.add(Pair(KEY, KEY_VALUE))

                /**
                 * 3. 拼串
                 */
                val content = generateGetParams(list)

                /**
                 * 4. md5之后拼到参数里
                 */
                param(SIGN, content.md5())
            }
            .build()

        return chain.proceed(request)
    }

    private fun generateGetParams(params: List<Pair<String, Any>>?): String {
        if (params.isNullOrEmpty()) {
            return ""
        }

        val sb = StringBuilder()
        var p: Pair<String, Any>?
        val size = params.size
        for (i in 0 until size) {
            p = params[i]
            sb.append(p.first)
            sb.append(SYMBOL_EQUAL)
            sb.append(p.second)

            if (i != size - 1) {
                sb.append(SYMBOL_AND)
            }
        }

        return sb.toString()
    }

}