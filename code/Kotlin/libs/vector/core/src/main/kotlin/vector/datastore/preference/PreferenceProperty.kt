package vector.datastore.preference

import androidx.datastore.preferences.core.Preferences
import vector.datastore.DataStoreOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class PreferenceProperty<V>(
    private val key: (String) -> Preferences.Key<V>,
    private val default: V? = null
) : ReadOnlyProperty<DataStoreOwner, DataStorePreference<V>> {

    private var cache: DataStorePreference<V>? = null

    override fun getValue(
        thisRef: DataStoreOwner,
        property: KProperty<*>
    ): DataStorePreference<V> {
        return cache ?: DataStorePreference(
            thisRef.dataStore,
            key(property.name),
            default
        ).also {
            cache = it
        }
    }
}

internal class SyncPreferenceProperty<V>(
    private val key: (String) -> Preferences.Key<V>,
    private val default: V? = null
) : ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<V>> {

    private var cache: SyncDataStorePreference<V>? = null

    override fun getValue(
        thisRef: DataStoreOwner,
        property: KProperty<*>
    ): SyncDataStorePreference<V> {
        return cache ?: SyncDataStorePreference(
            thisRef.dataStore,
            key(property.name),
            default
        ).also {
            cache = it
        }
    }
}