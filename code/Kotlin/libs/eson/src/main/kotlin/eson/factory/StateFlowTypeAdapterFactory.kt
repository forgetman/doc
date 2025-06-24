package eson.factory

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.lang.reflect.ParameterizedType

class StateFlowTypeAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
        val type = typeToken.type
        if (!StateFlow::class.java.isAssignableFrom(typeToken.rawType) || type !is ParameterizedType) {
            return null
        }
        val elementType = type.actualTypeArguments[0]
        val elementAdapter = gson.getAdapter(TypeToken.get(elementType))
        @Suppress("UNCHECKED_CAST")
        return newMultisetAdapter(elementAdapter) as TypeAdapter<T>
    }

    private fun <E> newMultisetAdapter(elementAdapter: TypeAdapter<E>): TypeAdapter<StateFlow<E>> {
        return object : TypeAdapter<StateFlow<E>>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: StateFlow<E>?) {
                if (value == null) {
                    out.nullValue()
                    return
                }
                elementAdapter.write(out, value.value)
            }

            @Throws(IOException::class)
            override fun read(reader: JsonReader): StateFlow<E>? {
                if (reader.peek() === JsonToken.NULL) {
                    reader.nextNull()
                    return null
                }
                val value = elementAdapter.read(reader)
                return MutableStateFlow(value)
            }
        }
    }
}