package bluetoothle.ext

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import compat.bluetooth.BluetoothCompat
import compat.context.ContextCompat
import compat.ext.getParcelableExtra
import compat.intent.IntentCompat
import logger.L
import no.nordicsemi.android.ble.BleManager
import sugar.ext.isSystemApplication


internal interface ParingObserver {
    fun registerParingListener()
    fun unregisterParingListener()
}

internal class ParingObserverImpl(private val context: Context) : ParingObserver {

    companion object {
        private const val LOG_TAG = "ParingImpl"
    }

    private var paringReceiverRegistered: Boolean = false
    private val paringReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device = IntentCompat.getParcelableExtra<BluetoothDevice>(intent, BluetoothDevice.EXTRA_DEVICE) ?: return

            when (intent.action) {
                BluetoothDevice.ACTION_PAIRING_REQUEST -> {
                    val type = intent.getIntExtra(
                        BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR
                    )
                    L.d(LOG_TAG, "PAIRING_REQUEST, type = $type")

                    val pin = intent.getIntExtra(
                        BluetoothDevice.EXTRA_PAIRING_KEY, 0
                    )

                    L.d(LOG_TAG, "PAIRING_REQUEST, pin = $pin")

                    /**
                     * [BluetoothDevice#PAIRING_VARIANT_PASSKEY]的调用无法通过编译, 原因未知, 改为使用[BleManager]的声明
                     */
                    val isSuccess = when (type) {
                        BleManager.PAIRING_VARIANT_PIN -> {
                            BluetoothCompat.setPin(device, pin.toString())
                        }

                        BleManager.PAIRING_VARIANT_PASSKEY -> {
                            BluetoothCompat.setPassKey(device, pin.toString())
                        }

                        BleManager.PAIRING_VARIANT_CONSENT,
                        BleManager.PAIRING_VARIANT_PASSKEY_CONFIRMATION -> {
                            BluetoothCompat.setPairingConfirmation(context, device, true)
                        }

                        else -> false
                    }

                    L.d(LOG_TAG, "PAIRING_REQUEST, success = $isSuccess")

                    if (isSuccess) {
                        abortBroadcast()
                        try {
                            context.sendBroadcast(Intent("android.bluetooth.device.action.PAIRING_CANCEL"))
                        } catch (e: Exception) {
                            L.e(LOG_TAG, "onReceive, send cancel broadcast", e)
                        }
                    }
                }
            }
        }
    }

    override fun registerParingListener() {
        if (!context.isSystemApplication(context.packageName)) return

        if (paringReceiverRegistered) return
        synchronized(this) {
            paringReceiverRegistered = true
        }
        ContextCompat.registerReceiver(context, paringReceiver, IntentFilter().apply {
            priority = IntentFilter.SYSTEM_HIGH_PRIORITY
            addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
        })
    }

    override fun unregisterParingListener() {
        if (!context.isSystemApplication(context.packageName)) return

        if (!paringReceiverRegistered) return
        synchronized(this) {
            paringReceiverRegistered = false
        }
        try {
            context.unregisterReceiver(paringReceiver)
        } catch (e: IllegalArgumentException) {
            L.e(LOG_TAG, "unregisterParingListener", e)
        }
    }
}