package vector.datastore.preference

import androidx.datastore.preferences.core.stringPreferencesKey
import eson.Eson
import eson.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import vector.datastore.DataStoreOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

// <editor-fold defaultstate = expanded" desc = "preference">
fun <T : Any> DataStoreOwner.objectPreference(
    default: T?,
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<String>> =
    PreferenceProperty(::stringPreferencesKey, serializer.toJson(default))

suspend fun <T : Any> DataStorePreference<String>.putObject(
    value: T?,
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
) {
    put(serializer.toJson(value))
}

fun <T : Any> DataStorePreference<String>.putInObject(
    coroutineScope: CoroutineScope,
    value: T?,
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
) {
    coroutineScope.launch {
        putObject(value, serializer)
    }
}

suspend inline fun <reified T : Any> DataStorePreference<String>.getObjectOrNull(
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
): T? {
    val json = getOrNull() ?: return null
    return serializer.fromJson(json, T::class)
}

suspend inline fun <reified T : Any> DataStorePreference<String>.getObjectOrElse(
    defaultValue: () -> T,
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
): T {
    return getObjectOrNull(serializer) ?: defaultValue()
}

@Throws(NullPointerException::class)
suspend inline fun <reified T : Any> DataStorePreference<String>.getObject(
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
): T {
    return getObjectOrNull(serializer) ?: throw NullPointerException("Object not found")
}
// </editor-fold>

// <editor-fold defaultstate = expanded" desc = "syncPreference">
fun <T : Any> DataStoreOwner.syncObjectPreference(
    default: T?,
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
): ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<String>> =
    SyncPreferenceProperty(::stringPreferencesKey, serializer.toJson(default))

fun <T : Any> SyncDataStorePreference<String>.putObject(
    value: T?,
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
) {
    put(serializer.toJson(value))
}

inline fun <reified T : Any> SyncDataStorePreference<String>.getObject(
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
): T? {
    val json = getOrNull() ?: return null
    return serializer.fromJson(json, T::class)
}
// </editor-fold>

inline fun <reified T : Any> DataStorePreference<String>.asObjectFlow(
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
): Flow<T?> {
    return asFlow().map { value ->
        if (value != null) {
            serializer.fromJson(value, T::class)
        } else null
    }
}

inline fun <reified T : Any> DataStorePreference<String>.defaultObject(
    serializer: PreferenceObjectSerializer = EsonPreferenceObjectSerializer
): T? {
    val json = default
    return if (json != null) {
        serializer.fromJson(json, T::class)
    } else null
}

// FIXME: 这种写法比较java, 还用的gson, 以后有时间可以考虑改成kolinx.serialization的方式
interface PreferenceObjectSerializer {
    fun toJson(value: Any?): String?
    fun <T : Any> fromJson(json: String, clazz: KClass<T>): T?
}

object EsonPreferenceObjectSerializer : PreferenceObjectSerializer {
    override fun toJson(value: Any?): String? {
        return value?.toJson()
    }

    override fun <T : Any> fromJson(
        json: String,
        clazz: KClass<T>
    ): T? {
        return Eson.default().fromJson(json, clazz.java)
    }
}