package eson.factory

import androidx.annotation.MainThread
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import live.Live
import sugar.ext.classOf

/**
 * 解析成[Live<Boolean>],[Live<Double>],[Live<Float>],[Live<Int>],[Live<Long>],[Live<String>]
 *
 * @author : GuoXuan
 * @since : 2019/2/22 0022
 */
class LiveTypeAdapterFactory : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>): TypeAdapter<T>? {
        return if (type.rawType.isLiveData()) {
            createAdapter(type)
        } else {
            null
        }
    }

    private fun <T> createAdapter(type: TypeToken<T>): TypeAdapter<T> {
        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter?, value: T?) {
                val data = value as? Live<*>
                val realValue = data?.value
                if (realValue == null) {
                    out?.nullValue()
                    return
                }
                when (realValue) {
                    is Byte -> out?.value(realValue)
                    is Short -> out?.value(realValue)
                    is Int -> out?.value(realValue)
                    is Long -> out?.value(realValue)
                    is Float -> out?.value(realValue)
                    is Double -> out?.value(realValue)
                    is Number -> out?.value(realValue)
                    is Boolean -> out?.value(realValue)
                    is String -> out?.value(realValue)
                    else -> {
                        // 暂不支持其他
                        // out?.value(gson?.toJson(it))
                    }
                }
            }

            @MainThread
            @Suppress("UNCHECKED_CAST")
            override fun read(reader: JsonReader?): T? {
                if (reader?.peek() == JsonToken.NULL) {
                    reader.nextNull()
                    return null
                } else {
                    var t: T? = null
                    when (type.rawType) {
                        classOf<Live<Boolean>>() -> {
                            val bool = reader?.nextBoolean()
                            t = Live(bool) as? T
                        }
                        classOf<Live<Double>>() -> {
                            val double = reader?.nextDouble()
                            t = Live(double) as? T
                        }
                        classOf<Live<Float>>() -> {
                            val float = reader?.nextDouble()?.toFloat()
                            t = Live(float) as? T
                        }
                        classOf<Live<Int>>() -> {
                            val int = reader?.nextInt()
                            t = Live(int) as? T
                        }
                        classOf<Live<Long>>() -> {
                            val long = reader?.nextLong()
                            t = Live(long) as? T
                        }
                        classOf<Live<String>>() -> {
                            val string = reader?.nextString()
                            t = Live(string) as? T
                        }
                        else -> {
                            // 暂不支持其他
                        }
                    }
                    return t
                }
            }

        }
    }

    /**
     * 是否(继承)LiveData
     */
    private fun Class<*>.isLiveData(): Boolean {
        return if (this == Live::class.java) {
            true
        } else {
            when (val clz = superclass) {
                null -> false
                Live::class.java -> true
                else -> clz.isLiveData()
            }
        }
    }
}
