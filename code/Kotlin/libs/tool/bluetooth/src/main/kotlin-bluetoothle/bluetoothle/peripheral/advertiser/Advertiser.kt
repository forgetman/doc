package bluetoothle.peripheral.advertiser

import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import compat.bluetooth.BluetoothCompat
import logger.L
import java.util.*


class Advertiser private constructor(
    private val context: Context,
    private val scanTimeOutMillis: Int,
    private val uuid: UUID,
    private val name: String?,
    private val advertiseMode: AdvertiseMode,
    private val callback: Callback?
) {
    companion object {
        private const val LOG_TAG = "BleAdvertiser"

        internal fun build(context: Context, action: Builder.() -> Unit): Advertiser {
            val builder = Builder()
            action(builder)
            return builder.build(context)
        }
    }

    interface Callback {
        fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {}

        /**
         * @param errorCode 来自[AdvertiseCallback]的内部定义
         */
        fun onStartFailure(errorCode: Int) {}
        fun onStopped() {}
    }

    enum class AdvertiseMode {
        LOW, // 低功率
        BALANCE, // 平衡
        HIGH // 高功率
    }

    private var started: Boolean = false
    private val innerCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            L.d(LOG_TAG, "onStartSuccess")
            callback?.onStartSuccess(settingsInEffect)
        }

        override fun onStartFailure(errorCode: Int) {
            L.d(LOG_TAG, "onStartFailure = $errorCode")
            callback?.onStartFailure(errorCode)
            started = false
        }
    }

    private fun settings(): AdvertiseSettings {
        return AdvertiseSettings.Builder().apply {
            val mode = when (advertiseMode) {
                AdvertiseMode.LOW -> AdvertiseSettings.ADVERTISE_MODE_LOW_POWER
                AdvertiseMode.BALANCE -> AdvertiseSettings.ADVERTISE_MODE_BALANCED
                AdvertiseMode.HIGH -> AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
            }
            setAdvertiseMode(mode)
            setConnectable(true)
            setTimeout(scanTimeOutMillis)
            setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
        }.build()
    }

    private fun advertiseData(): AdvertiseData {
        return AdvertiseData.Builder().apply {
            setIncludeTxPowerLevel(false)
            addServiceUuid(ParcelUuid(uuid))
            if (name != null) {
                setIncludeDeviceName(true) // Including it will blow the length
            } else {
                setIncludeDeviceName(false)
            }
        }.build()
    }

    fun start() {
        if (started) return
        started = true
        BluetoothCompat.Ble.startAdvertising(context, settings(), advertiseData(), innerCallback)
    }

    fun stop() {
        if (!started) return
        started = false
        BluetoothCompat.Ble.stopAdvertising(context, innerCallback)
        callback?.onStopped()
    }

    class Builder {
        var uuid: UUID? = null
        var name: String? = null
        var advertiseMode: AdvertiseMode = AdvertiseMode.LOW

        /**
         * 广播时长, 最多180000毫秒。值为0将禁用时间限制
         */
        var scanTimeOutMillis = 0
        var callback: Callback? = null

        fun build(context: Context): Advertiser {
            return Advertiser(
                context,
                scanTimeOutMillis,
                uuid ?: UUID.randomUUID(),
                name,
                advertiseMode,
                callback
            )
        }
    }
}