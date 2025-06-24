package eson

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import eson.factory.EnumTypeAdapterFactory
import eson.factory.LiveTypeAdapterFactory
import eson.factory.StateFlowTypeAdapterFactory
import logger.L
import java.lang.reflect.Type

/**
 * gson的装饰类
 */
class Eson private constructor(
    fs: List<TypeAdapterFactory>? = null,
    escapeHtmlChars: Boolean = true
) {
    companion object {
        private const val LOG_TAG = "Eson"

        private val DEFAULT_OBJECT = Eson()

        @JvmStatic
        fun create(fs: List<TypeAdapterFactory>? = null, escapeHtmlChars: Boolean = true): Eson {
            return Eson(fs, escapeHtmlChars)
        }

        @JvmStatic
        @JvmName("getDefault")
        fun default() = DEFAULT_OBJECT
    }

    private val factories = mutableListOf<TypeAdapterFactory>()
    private var escapeHtmlChars: Boolean = false

    private lateinit var gson: Gson

    init {
        if (fs != null) {
            val filterFactories = fs.filter {
                it !is EnumTypeAdapterFactory
                        && it !is LiveTypeAdapterFactory
                        && it !is StateFlowTypeAdapterFactory
            }
            factories.addAll(filterFactories)
        }

        this.escapeHtmlChars = escapeHtmlChars
        build()
    }

    private fun addDefaultFactories() {
        factories.add(EnumTypeAdapterFactory())
        factories.add(LiveTypeAdapterFactory())
        factories.add(StateFlowTypeAdapterFactory())
    }

    private fun build() {
        val builder = GsonBuilder()
            .setExclusionStrategies(object : ExclusionStrategy {
                override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                    return false
                }

                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    return f?.getAnnotation(Ignore::class.java) != null
                }
            })

        if (escapeHtmlChars) builder.disableHtmlEscaping()

        addDefaultFactories()
        factories.forEach {
            builder.registerTypeAdapterFactory(it)
        }

        gson = builder.create()
    }

    fun <T> fromJson(json: String?, type: Type): T? {
        return try {
            gson.fromJson<T>(json, type)
        } catch (e: JsonSyntaxException) {
            L.e(LOG_TAG, "fromJson", e)
            null
        }
    }

    fun <T> fromJson(json: String?, classOfT: Class<T>): T? {
        return try {
            gson.fromJson(json, classOfT)
        } catch (e: JsonSyntaxException) {
            L.e(LOG_TAG, "fromJson", e)
            null
        }
    }

    inline fun <reified T> fromJson(json: String?): T? {
        val typeToken = object : TypeToken<T>() {}
        val type: Type = typeToken.type
        return fromJson<T>(json, type)
    }

    fun toJson(any: Any?): String {
        return gson.toJson(any)
    }
}

inline fun <reified T> String?.fromJson(): T? {
    return Eson.default().fromJson<T>(this)
}

fun Any?.toJson(): String = Eson.default().toJson(this)