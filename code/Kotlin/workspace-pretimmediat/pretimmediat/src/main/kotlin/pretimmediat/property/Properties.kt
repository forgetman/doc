package pretimmediat.property

import vector.datastore.DataStoreOwner
import vector.datastore.preference.booleanPreference
import vector.datastore.preference.stringPreference

/**
 * 全局设置
 */
object Properties : DataStoreOwner by DataStoreOwner("settings") {
    val accountId by stringPreference("")
    val accountToken by stringPreference("")
    val accountTest by booleanPreference(true)
    val accountPhoneNumber by stringPreference("")

    val pieceGivenName by stringPreference("")
    val pieceFaceUrl by stringPreference("")

    val location by stringPreference("")
    val latitude by stringPreference("")
    val longitude by stringPreference("")

    val language by stringPreference("fr")
    val gaid by stringPreference("00000000-0000-0000-0000-000000000000")
    val afId by stringPreference("")
    val appInstanceId by stringPreference("")
    val ip by stringPreference("")
    val showPermission by booleanPreference(true)
    val showPermissionSecond by booleanPreference(true)
}