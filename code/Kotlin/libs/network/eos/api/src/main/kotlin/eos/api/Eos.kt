@file:Suppress("unused")

package eos.api

import eos.api.model.IntervalTime
import eos.api.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import logger.L
import sugar.collection.safeMutableListOf
import java.util.concurrent.ConcurrentLinkedQueue


class Eos(
    private val webSocketClient: WebSocketClient,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    companion object {
        private const val LOG_TAG = "Eos"
    }

    private val messageQueue = ConcurrentLinkedQueue<Message>()
    private var isConnected = false
    private var manualDisconnect = false
    private val intervalTime = IntervalTime()

    private var sendingJob: Job? = null
    private var reconnectJob: Job? = null

    private val listeners = safeMutableListOf<WebSocketListener>()


    init {
        setupListener()
    }

    private fun setupListener() {
        webSocketClient.setListener(object : WebSocketListener {
            override fun onOpen() {
                L.d(LOG_TAG, "onOpen, Connected to WebSocket")
                isConnected = true
                intervalTime.reset()
                startSendingMessages()

                listeners.forEach { it.onOpen() }
            }

            override fun onMessage(message: String) {
                L.d(LOG_TAG, "onMessage, Received message: $message")
                listeners.forEach { it.onMessage(message) }
            }

            override fun onMessage(message: ByteArray) {
                L.d(LOG_TAG, "onMessage, Received bytes message size: ${message.size}")
                listeners.forEach { it.onMessage(message) }
            }

            override fun onClose(code: Int, reason: String) {
                L.d(LOG_TAG, "onClose, Connection closed: $code, $reason")
                isConnected = false
                stopSendingMessages()
                if (!manualDisconnect) scheduleReconnect()
                listeners.forEach { it.onClose(code, reason) }
            }

            override fun onFailure(t: Throwable) {
                L.d(LOG_TAG, "onFailure, Connection failed: ${t.message}")
                isConnected = false
                stopSendingMessages()
                if (!manualDisconnect) scheduleReconnect()
                listeners.forEach { it.onFailure(t) }
            }
        })
    }

    fun connect() {
        manualDisconnect = false
        webSocketClient.connect()
    }

    fun disconnect() {
        manualDisconnect = true
        webSocketClient.disconnect()
        stopSendingMessages()
        reconnectJob?.cancel()
    }

    fun sendMessage(message: String) {
        messageQueue.offer(Message.TextMessage(message))
    }

    fun sendMessage(bytes: ByteArray) {
        messageQueue.offer(Message.BinaryMessage(bytes))
    }

    fun clear() {
        messageQueue.clear()
    }

    fun release() {
        disconnect()
        listeners.clear()
        messageQueue.clear()
        webSocketClient.release()
    }

    fun addListener(listener: WebSocketListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: WebSocketListener) {
        listeners.remove(listener)
    }

    private fun startSendingMessages() {
        if (sendingJob?.isActive == true) return
        sendingJob = scope.launch {
            while (isConnected && isActive) {
                val message = messageQueue.peek()
                if (message == null) {
                    // 稍微延迟一下，避免过于频繁的检查
                    delay(100)
                    continue
                }
                val success = when (message) {
                    is Message.TextMessage -> webSocketClient.send(message.content)
                    is Message.BinaryMessage -> webSocketClient.send(message.content)
                }
                if (!success) {
                    L.d(LOG_TAG, "startSendingMessages, Failed to send message: $message")
                } else {
                    messageQueue.poll()
                }
            }
        }
    }

    private fun stopSendingMessages() {
        sendingJob?.cancel()
        sendingJob = null
    }

    private fun scheduleReconnect() {
        if (reconnectJob?.isActive == true) return
        reconnectJob = scope.launch {
            val interval = intervalTime.getInterval()
            L.d(LOG_TAG, "scheduleReconnect, Reconnecting in $interval ms...")
            delay(interval)
            if (!manualDisconnect) {
                L.d(LOG_TAG, "scheduleReconnect, Attempting to reconnect...")
                if (webSocketClient.connect()) {
                    L.d(LOG_TAG, "scheduleReconnect, Reconnected successfully")
                    intervalTime.reset()
                } else {
                    L.d(LOG_TAG, "scheduleReconnect, Reconnect failed")
                    intervalTime.nextBackoff()
                    scheduleReconnect()
                }
            }
        }
    }
}