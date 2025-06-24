package test.model

import vector.datastore.DataStoreOwner
import vector.datastore.preference.enumPreference
import vector.datastore.preference.objectPreference
import vector.datastore.preference.stringPreference
import vector.datastore.preference.syncStringPreference

/**
 * @author yuansui
 * @since 2024/10/16
 */
class UserRepo : DataStoreOwner by DataStoreOwner("user") {
    val name by stringPreference(null)
    val testEnum by enumPreference(TestEnum.A)
    val user by objectPreference(null)
    val syncName by syncStringPreference(null)
}