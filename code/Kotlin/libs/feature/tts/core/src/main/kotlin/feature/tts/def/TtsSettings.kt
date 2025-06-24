package feature.tts.def

import vector.datastore.DataStoreOwner
import vector.datastore.MultiProcessDataStoreOwner
import vector.datastore.preference.booleanPreference

object TtsSettings : DataStoreOwner by MultiProcessDataStoreOwner("function.tts") {
    val useRobotic by booleanPreference(false)
    val debuggable by booleanPreference(false)
}