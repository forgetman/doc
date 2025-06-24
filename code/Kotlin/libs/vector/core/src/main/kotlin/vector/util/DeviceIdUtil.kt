package vector.util

import android.content.SharedPreferences
import androidx.core.content.edit
import logger.L
import vector.appContext
import vector.ext.isNotNullOrEmpty
import java.util.UUID

/**
 * 生成设备唯一ID的工具
 */
object DeviceIdUtil {
    private const val PREF_FILE = "device_id.xml"
    private const val PREF_DEVICE_ID = "device_id"

    private var innerId: String? = ""

    val id: String
        get() {
            val existId = innerId
            if (existId.isNotNullOrEmpty()) return existId

            val context = appContext
            var id: String?

            val pref = context.getSharedPreferences(PREF_FILE, 0)
            id = pref.getString(PREF_DEVICE_ID, null)
            if (id != null) {
                innerId = id
                return id
            }

            try {
                id = DeviceUtil.getAndroidId(context)
                if (!id.isNullOrEmpty() && id != "9774d56d682e549c") {
                    return putId(pref, UUID.nameUUIDFromBytes(id.toByteArray()))
                }

                return putId(pref, UUID.randomUUID())
            } catch (e: Exception) {
                L.e(e)
            }

            return putId(pref, UUID.randomUUID())
        }

    private fun putId(pref: SharedPreferences, uuid: UUID): String {
        val newId = uuid.toString()
        pref.edit { putString(PREF_DEVICE_ID, newId) }
        innerId = newId
        return newId
    }
}