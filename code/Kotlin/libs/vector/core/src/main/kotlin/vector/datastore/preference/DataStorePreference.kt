package vector.datastore.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import coroutine.flow.launchForever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException

interface DataStorePreferenceOwner<V> {
    val dataStore: DataStore<Preferences>
    val key: Preferences.Key<V>
    val default: V?

    fun asFlow(): Flow<V?> = dataStore.data.map { it[key] ?: default }

    fun asFirstFlow(): Flow<V?> = callbackFlow {
        val prefs = dataStore.data.firstOrNull()
        if (prefs != null) {
            send(prefs[key] ?: default)
        } else {
            send(default)
        }
        close()
    }
}

class DataStorePreference<V>(
    override val dataStore: DataStore<Preferences>,
    override val key: Preferences.Key<V>,
    override val default: V?
) : DataStorePreferenceOwner<V> {

    suspend fun put(value: V?) {
        dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(key)
            } else {
                preferences[key] = value
            }
        }
    }

    fun putIn(coroutineScope: CoroutineScope, value: V?) {
        coroutineScope.launch {
            put(value)
        }
    }

    suspend fun getOrNull(): V? = asFlow().firstOrNull()

    suspend fun getOrPut(defaultValue: () -> V): V = getOrNull() ?: defaultValue().also { put(it) }

    suspend fun remove(): V? {
        val preference = dataStore.edit { preferences ->
            preferences.remove(key)
        }
        return preference[key]
    }
}

/**
 * 同步存储方式的Preference, 特殊场景适用
 */
class SyncDataStorePreference<V>(
    override val dataStore: DataStore<Preferences>,
    override val key: Preferences.Key<V>,
    override val default: V?
) : DataStorePreferenceOwner<V> {

    fun put(value: V?) {
        callbackFlow {
            val prefs = dataStore.edit { preferences ->
                if (value == null) {
                    preferences.remove(key)
                } else {
                    preferences[key] = value
                }
            }
            send(prefs)
            close()
        }.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            }
        }.launchForever()
    }

    fun getOrNull(): V? = runBlocking {
        dataStore.data.first()[key] ?: default
    }

    fun getOrElse(defaultValue: () -> V): V = runBlocking {
        dataStore.data.first()[key] ?: defaultValue()
    }

    fun get(): V = runBlocking {
        dataStore.data.first()[key] ?: default ?: throw NullPointerException("Preference $key is null")
    }

    fun remove(): V? {
        val preference = runBlocking {
            dataStore.edit { preferences ->
                preferences.remove(key)
            }
        }
        return preference[key]
    }
}

fun <V> DataStorePreference<V>.sync(): SyncDataStorePreference<V> {
    return SyncDataStorePreference(dataStore, key, default)
}