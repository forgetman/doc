package vector.datastore.preference

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import vector.datastore.DataStoreOwner
import kotlin.properties.ReadOnlyProperty

// <editor-fold defaultstate = expanded" desc = "preference">
fun DataStoreOwner.intPreference(
    default: Int?
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<Int>> =
    PreferenceProperty(::intPreferencesKey, default)

fun DataStoreOwner.doublePreference(
    default: Double?
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<Double>> =
    PreferenceProperty(::doublePreferencesKey, default)

fun DataStoreOwner.longPreference(
    default: Long?
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<Long>> =
    PreferenceProperty(::longPreferencesKey, default)

fun DataStoreOwner.floatPreference(
    default: Float?
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<Float>> =
    PreferenceProperty(::floatPreferencesKey, default)

fun DataStoreOwner.booleanPreference(
    default: Boolean?
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<Boolean>> =
    PreferenceProperty(::booleanPreferencesKey, default)

fun DataStoreOwner.stringPreference(
    default: String?
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<String>> =
    PreferenceProperty(::stringPreferencesKey, default)

fun DataStoreOwner.stringSetPreference(
    default: Set<String>?
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<Set<String>>> =
    PreferenceProperty(::stringSetPreferencesKey, default)

fun DataStoreOwner.byteArrayPreference(
    default: ByteArray?
): ReadOnlyProperty<DataStoreOwner, DataStorePreference<ByteArray>> =
    PreferenceProperty(::byteArrayPreferencesKey, default)
// </editor-fold>

// <editor-fold defaultstate = expanded" desc = "syncPreference">
fun DataStoreOwner.syncIntPreference(default: Int?)
        : ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<Int>> =
    SyncPreferenceProperty(::intPreferencesKey, default)

fun DataStoreOwner.syncDoublePreference(
    default: Double?
): ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<Double>> =
    SyncPreferenceProperty(::doublePreferencesKey, default)

fun DataStoreOwner.syncLongPreference(
    default: Long?
): ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<Long>> =
    SyncPreferenceProperty(::longPreferencesKey, default)

fun DataStoreOwner.syncFloatPreference(
    default: Float?
): ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<Float>> =
    SyncPreferenceProperty(::floatPreferencesKey, default)

fun DataStoreOwner.syncBooleanPreference(
    default: Boolean?
): ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<Boolean>> =
    SyncPreferenceProperty(::booleanPreferencesKey, default)

fun DataStoreOwner.syncStringPreference(
    default: String?
): ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<String>> =
    SyncPreferenceProperty(::stringPreferencesKey, default)

fun DataStoreOwner.syncStringSetPreference(
    default: Set<String>?
): ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<Set<String>>> =
    SyncPreferenceProperty(::stringSetPreferencesKey, default)

fun DataStoreOwner.syncByteArrayPreference(
    default: ByteArray?
): ReadOnlyProperty<DataStoreOwner, SyncDataStorePreference<ByteArray>> =
    SyncPreferenceProperty(::byteArrayPreferencesKey, default)
// </editor-fold>