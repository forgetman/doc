package test.model

import vector.datastore.DataStoreOwner
import vector.datastore.MultiProcessDataStoreOwner
import vector.datastore.preference.enumPreference
import vector.datastore.preference.objectPreference
import vector.datastore.preference.stringPreference

class MultiUserRepo : DataStoreOwner by MultiProcessDataStoreOwner("multi_user") {
    val name by stringPreference(null)
    val testEnum by enumPreference(TestEnum.A)
    val user by objectPreference(null)
}