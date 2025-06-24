package vector.datastore.preference

import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import vector.datastore.DataStoreOwner
import kotlin.properties.ReadOnlyProperty

// <editor-fold defaultstate = expanded" desc = "preference">
fun <E : Enum<E>> DataStoreOwner.enumPreference(
    default: E?
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<String>> =
    PreferenceProperty(::stringPreferencesKey, default?.name)

suspend fun <E : Enum<E>> DataStorePreference<String>.putEnum(value: E?) {
    put(value?.name)
}

fun <E : Enum<E>> DataStorePreference<String>.putInEnum(coroutineScope: CoroutineScope, value: E?) {
    coroutineScope.launch {
        put(value?.name)
    }
}

suspend inline fun <reified E : Enum<E>> DataStorePreference<String>.getEnumOrNull(): E? {
    val enumName = getOrNull()
    val enums: Array<E>? = E::class.java.enumConstants
    return enums?.firstOrNull { it.name == enumName }
}

suspend inline fun <reified E : Enum<E>> DataStorePreference<String>.getEnumOrElse(defaultValue: () -> E): E {
    return getEnumOrNull<E>() ?: defaultValue()
}

@Throws(NullPointerException::class)
suspend inline fun <reified E : Enum<E>> DataStorePreference<String>.getEnum(): E {
    return getEnumOrNull<E>() ?: throw NullPointerException("Enum not found")
}
// </editor-fold>

// <editor-fold defaultstate = expanded" desc = "syncPreference">
fun <E : Enum<E>> DataStoreOwner.syncEnumPreference(
    default: E?
): ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<String>> =
    SyncPreferenceProperty(::stringPreferencesKey, default?.name)

fun <E : Enum<E>> SyncDataStorePreference<String>.putEnum(value: E?) {
    put(value?.name)
}

inline fun <reified E : Enum<E>> SyncDataStorePreference<String>.getEnumOrNull(): E? {
    val enumName = getOrNull()
    val enums: Array<E>? = E::class.java.enumConstants
    return enums?.firstOrNull { it.name == enumName }
}
// </editor-fold>

inline fun <reified E : Enum<E>> DataStorePreference<String>.asEnumFlow(): Flow<E?> {
    val enums: Array<E>? = E::class.java.enumConstants
    return asFlow().map { enumName ->
        enums?.firstOrNull { it.name == enumName }
    }
}

inline fun <reified E : Enum<E>> DataStorePreference<String>.asEnumFirstFlow(): Flow<E?> {
    val enums: Array<E>? = E::class.java.enumConstants
    return asFirstFlow().map { enumName ->
        enums?.firstOrNull { it.name == enumName }
    }
}

inline fun <reified E : Enum<E>> DataStorePreference<String>.defaultEnum(): E? {
    val enumName = default
    val enums: Array<E>? = E::class.java.enumConstants
    return enums?.firstOrNull { it.name == enumName }
}