package eson.factory

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * gson支持解析成枚举的方式
 */
internal class EnumTypeAdapterFactory : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>): TypeAdapter<T>? {
        return if (type.rawType.isEnum) {
            EnumAdapter(type)
        } else {
            null
        }
    }
}

@Suppress("UNCHECKED_CAST")
internal class EnumAdapter<T>(private val type: TypeToken<T>) : TypeAdapter<T>() {

    override fun write(out: JsonWriter, value: T?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    override fun read(reader: JsonReader): T? {
        return if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            null
        } else {
            val source = reader.nextString()
            val enum: T? = type.rawType.enumConstants?.filterNotNull()?.find {
                it.toString() == source
            } as T?

            if (enum == null) {
                reader.nextNull()
            }
            enum
        }
    }

}