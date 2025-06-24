package eos.api

interface WebSocketListener {
    fun onOpen() {}
    fun onMessage(message: String) {}
    fun onMessage(message: ByteArray) {}
    fun onClose(code: Int, reason: String) {}
    fun onFailure(t: Throwable) {}
}