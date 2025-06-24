package compat.telephony.api.delegate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import compat.context.ContextCompat
import compat.telephony.api.Api
import compat.telephony.def.SimState
import compat.telephony.def.listener.SimStateListener
import coroutine.flow.launchForever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import logger.L
import sugar.collection.safeMutableListOf

internal interface SimStateDelegate {
    fun addSimStateListener(context: Context, api: Api, listener: SimStateListener): Boolean
    fun removeSimStateListener(context: Context, listener: SimStateListener): Boolean
}

/**
 * @author yuansui
 * @since 2023/6/7
 */
internal object SimStateDelegateImpl : SimStateDelegate {
    private const val LOG_TAG = "SimStateDelegate"

    private var simStateListeners = safeMutableListOf<SimStateListener>()
    private val simState = MutableStateFlow(SimState.UNKNOWN)
    private var simStateJob: Job? = null
    private var receiver: BroadcastReceiver? = null


    override fun addSimStateListener(context: Context, api: Api, listener: SimStateListener): Boolean {
        if (simStateListeners.contains(listener)) return false
        simStateListeners.add(listener)

        val result = simStateListeners.size <= 1
        if (result) {
            reset(context)

            simStateJob = simState.onEach { state ->
                simStateListeners.forEachElement { l ->
                    l.onStateChanged(state)
                }
            }.flowOn(Dispatchers.Main.immediate).launchForever()

            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent?) {
                    val simState = api.getSimState(context)
                    L.d(LOG_TAG, "onReceive, simState: $simState")
                    this@SimStateDelegateImpl.simState.value = simState
                }
            }
            ContextCompat.registerReceiver(context, receiver, IntentFilter().apply {
                priority = IntentFilter.SYSTEM_HIGH_PRIORITY
                addAction("android.intent.action.SIM_STATE_CHANGED")
            })
        } else {
            // 因为用的是StateFlow, 可能会因为值相同而不进行首次回调, 所以先手动回调一次
            listener.onStateChanged(simState.value)
        }
        return result
    }

    override fun removeSimStateListener(context: Context, listener: SimStateListener): Boolean {
        val result = simStateListeners.remove(listener)
        if (simStateListeners.isEmpty()) {
            L.d(LOG_TAG, "removeSimCardStateListener, simCardListeners is empty")
            reset(context)
        }
        return result
    }

    private fun reset(context: Context) {
        simStateJob?.cancel()
        simStateJob = null

        receiver?.let {
            context.unregisterReceiver(it)
            receiver = null
        }
    }
}