package eos.api

interface WebSocketClient {
    fun connect(): Boolean
    fun disconnect()
    fun send(message: String): Boolean
    fun send(message: ByteArray): Boolean
    fun setListener(listener: WebSocketListener)
    fun isConnected(): Boolean
    fun release()
}
