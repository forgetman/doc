package catroom.datastore

import catroom.def.Resolution
import vector.datastore.DataStoreOwner
import vector.datastore.preference.doublePreference
import vector.datastore.preference.enumPreference
import vector.datastore.preference.stringPreference

/**
 * 全局设置
 */
object Properties : DataStoreOwner by DataStoreOwner("properties") {
    val roomLongitude by doublePreference(0.0)
    val roomLatitude by doublePreference(0.0)
    val roomName by stringPreference(null)

    val cameraFrontResolution by enumPreference<Resolution>(null)
    val cameraBackResolution by enumPreference<Resolution>(null)
}