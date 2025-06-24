package vector.datastore

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import vector.appContext
import java.util.concurrent.ConcurrentHashMap

interface DataStoreOwner {
    val context: Context
    val dataStore: DataStore<Preferences>

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

private val dataStoreMap = ConcurrentHashMap<String, DataStore<Preferences>>()
private val multiDataStoreMap = ConcurrentHashMap<String, DataStore<Preferences>>()

fun DataStoreOwner(
    name: String,
    context: Context = appContext,
    produceMigrations: (Context) -> List<DataMigration<Preferences>> = { listOf() },
): DataStoreOwner = DataStoreOwnerImpl(name, context, produceMigrations)

private class DataStoreOwnerImpl(
    private val name: String,
    override val context: Context,
    produceMigrations: (Context) -> List<DataMigration<Preferences>>
) : DataStoreOwner {
    private val Context.dataStore by preferencesDataStore(name, produceMigrations = produceMigrations)

    override val dataStore: DataStore<Preferences>
        get() {
            return dataStoreMap.getOrPut(name) { context.dataStore }
        }
}

@Suppress("FunctionName")
fun MultiProcessDataStoreOwner(
    name: String,
    context: Context = appContext,
    produceMigrations: (Context) -> List<DataMigration<Preferences>> = { listOf() }
): DataStoreOwner = MultiProcessDataStoreOwnerImpl(name, context, produceMigrations)

private class MultiProcessDataStoreOwnerImpl(
    private val name: String,
    override val context: Context,
    produceMigrations: (Context) -> List<DataMigration<Preferences>>
) : DataStoreOwner {
    private val Context.multiDataStore by multiProcessPreferencesDataStore(name, produceMigrations = produceMigrations)

    override val dataStore: DataStore<Preferences>
        get() {
            return multiDataStoreMap.getOrPut(name) { context.multiDataStore }
        }
}