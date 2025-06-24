package compat.network.def.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import compat.context.ContextCompat
import sugar.collection.safeMutableListOf

internal typealias OnReceiveAction<T> = ReceiverListener<T>.(context: Context, intent: Intent) -> Unit

internal class ReceiverListener<T>(
    private val actions: List<String>,
    private val onReceiveAction: OnReceiveAction<T>
) {
    constructor(vararg actions: String, onReceiveAction: OnReceiveAction<T>) : this(
        actions.toList(),
        onReceiveAction
    )

    private var receiver: BroadcastReceiver? = null
    private val listeners = safeMutableListOf<T>()

    fun add(context: Context, listener: T): Boolean {
        if (listeners.contains(listener)) return false
        val result = listeners.add(listener)
        if (result) {
            register(context)
        }
        return result
    }

    fun remove(context: Context, listener: T): Boolean {
        val remove = listeners.remove(listener)
        if (remove && listeners.isEmpty()) {
            unregister(context)
        }
        return remove
    }

    fun forEach(action: (T) -> Unit) {
        listeners.forEachElement(action)
    }

    @Synchronized
    private fun register(context: Context) {
        if (receiver != null) return
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                onReceiveAction(context, intent)
            }
        }
        val initialIntent = ContextCompat.registerReceiver(context, receiver, IntentFilter().apply {
            actions.forEach { addAction(it) }
        })
        if (initialIntent != null) {
            onReceiveAction(context, initialIntent)
        }
    }

    @Synchronized
    private fun unregister(context: Context) {
        if (receiver == null) return
        context.unregisterReceiver(receiver)
        receiver = null
    }
}