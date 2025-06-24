package bluetoothle.def

import logger.L
import java.util.*

/**
 * 通用UUID声明
 * @author yuansui
 * @since 2021/10/22
 */
object BleUUID {

    object Descriptor {
        const val EXTENDED_PROPERTIES = "00002900-0000-1000-8000-00805f9b34fb"
        const val USER_DESCRIPTION = "00002901-0000-1000-8000-00805f9b34fb"
        const val CLIENT_CONFIGURATION = "00002902-0000-1000-8000-00805f9b34fb"
        const val SERVER_CONFIGURATION = "00002903-0000-1000-8000-00805f9b34fb"
    }

    object IOS {
        const val ANCS = "7905f431-b5ce-4e99-a40f-4b1e122d00d0"
        const val CONTROL = "69d1d8f3-45e1-49a8-9821-9bbdfdaad9d9"
        const val DATA = "22eac6e9-24d6-4bb5-be44-b36ace7c7bfb"
        const val NOTIFICATION = "9fbf120d-6301-42d9-8c58-25e699a21dbd"
    }
}

fun String.toUUID(): UUID? {
    val components = this.split("-")
    if (components.size != 5) {
        L.e("Invalid UUID name: $this")
        return null
    }
    return UUID.fromString(this)
}