package bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import compat.bluetooth.BluetoothCompat
import compat.context.ContextCompat
import logger.L
import sugar.collection.safeMutableListOf
import sugar.ext.runOnCurrThread

/**
 * @author yuansui
 * @since 2023/3/20
 */
class BluetoothStateHelper(private val context: Context) {

    companion object {
        private const val LOG_TAG = "bluetooth.BluetoothState"
    }

    enum class State {
        TURNING_ON, // 正在打开
        ON, // 已打开
        TURNING_OFF, // 正在关闭
        OFF, // 已关闭
    }

    fun interface Listener {
        fun onStateChanged(state: State)
    }

    private var receiver: BroadcastReceiver? = null
    private val listeners = safeMutableListOf<Listener>()
    var currentState: State = State.OFF
        private set


    init {
        runOnCurrThread {
            // 防止context还没初始化完, 延迟使用
            currentState = if (BluetoothCompat.isEnabled(context)) State.ON else State.OFF
            listeners.forEachElement {
                it.onStateChanged(currentState)
            }
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
        listener.onStateChanged(currentState)
        if (receiver != null) return

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            L.d(LOG_TAG, "onStateChanged, state = TURNING_ON")
                            currentState = State.TURNING_ON
                        }

                        BluetoothAdapter.STATE_ON -> {
                            L.d(LOG_TAG, "onStateChanged, state = ON")
                            currentState = State.ON
                        }

                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            L.d(LOG_TAG, "onStateChanged, state = TURNING_OFF")
                            currentState = State.TURNING_OFF
                        }

                        BluetoothAdapter.STATE_OFF -> {
                            L.d(LOG_TAG, "onStateChanged, state = OFF")
                            currentState = State.OFF
                        }
                    }
                    listeners.forEachElement {
                        it.onStateChanged(currentState)
                    }
                }
            }
        }

        ContextCompat.registerReceiver(context, receiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        })
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
        unregisterReceiverIfNeeded()
    }

    fun clear() {
        listeners.clear()
        unregisterReceiverIfNeeded()
    }

    private fun unregisterReceiverIfNeeded() {
        if (listeners.isEmpty()) {
            receiver?.let {
                context.unregisterReceiver(it)
                receiver = null
            }
        }
    }
}