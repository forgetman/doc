package bluetoothle.ext

import bluetoothle.def.Constants
import logger.L
import no.nordicsemi.android.ble.WriteRequest
import no.nordicsemi.android.ble.data.DataSplitter
import sugar.ext.self
import kotlin.math.min

private const val SPLIT_SIZE: Int = Constants.MTU_MIN - Constants.MTU_HEADER_INFO_LENGTH
private const val LOG_TAG = "request_split"

internal fun WriteRequest.splitIfNeeded(value: ByteArray?, limit: Int) = self {
    val needSplit: Boolean = (value?.size ?: 0) > limit
    L.d(LOG_TAG, "write needSplit = $needSplit")
    val splitter = object : DataSplitter {

        override fun chunk(message: ByteArray, index: Int, maxLength: Int): ByteArray? {
            val offset = index * SPLIT_SIZE
            val length = min(SPLIT_SIZE, message.size - offset)

            if (length <= 0) return null

            val data = ByteArray(length)
            System.arraycopy(message, offset, data, 0, length)
            return data
        }
    }

    if (needSplit) split(splitter) { _, _, index ->
        L.d(LOG_TAG, "分段发送进度 = $index")
    }
}