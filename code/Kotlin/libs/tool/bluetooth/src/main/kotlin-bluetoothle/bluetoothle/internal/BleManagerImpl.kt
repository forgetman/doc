package bluetoothle.internal

import android.content.Context
import bluetoothle.def.Constants
import logger.L
import no.nordicsemi.android.ble.BleManager

/**
 * @author yuansui
 * @since 2023/3/17
 */
internal open class BleManagerImpl(context: Context) : BleManager(context) {

    companion object {
        private const val LOG_TAG = "BleManagerImpl"
    }

    public override fun getMtu(): Int {
        return super.getMtu()
    }

    fun requestMtu(callback: (splitSize: Int) -> Unit) {
        requestMtu(Constants.MTU_MAX).with { _, mtu ->
            L.d(LOG_TAG, "request mtu success, new mtu = $mtu")
            val splitSize = mtu - Constants.MTU_HEADER_INFO_LENGTH
            callback(splitSize)
        }.done {
            L.d(LOG_TAG, "request mtu done")
        }.fail { _, status ->
            L.e(LOG_TAG, "request mtu fail, status = $status")
        }.enqueue()
    }
}