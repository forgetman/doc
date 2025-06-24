package eos.api.impl

import eos.api.WebSocketClient
import eos.api.WebSocketListener
import okhttp.ext.ssl
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class OkhttpWebSocketClient private constructor(
    private val okHttpClient: OkHttpClient,
    private val url: String,
    private val headers: Map<String, String>
) : WebSocketClient {

    private var webSocket: WebSocket? = null
    private var listener: WebSocketListener? = null
    private var isConnected = false

    class Builder {
        private var okHttpClient: OkHttpClient? = null
        private var url: String = ""
        private val headers: MutableMap<String, String> = mutableMapOf()
        private var connectTimeout: Long = 10
        private var readTimeout: Long = 10
        private var writeTimeout: Long = 10
        private var sslSocketFactory: SSLSocketFactory? = null
        private var trustManager: X509TrustManager? = null

        fun client(client: OkHttpClient) = apply { this.okHttpClient = client }
        fun url(url: String) = apply { this.url = url }
        fun header(key: String, value: String) = apply { headers[key] = value }
        fun headers(headers: Map<String, String>) = apply { this.headers.putAll(headers) }
        fun connectTimeout(timeout: Long) = apply { this.connectTimeout = timeout }
        fun readTimeout(timeout: Long) = apply { this.readTimeout = timeout }
        fun writeTimeout(timeout: Long) = apply { this.writeTimeout = timeout }
        fun sslSocketFactory(sslSocketFactory: SSLSocketFactory, trustManager: X509TrustManager) = apply {
            this.sslSocketFactory = sslSocketFactory
            this.trustManager = trustManager
        }

        fun build(): OkhttpWebSocketClient {
            require(url.isNotEmpty()) { "WebSocket URL must be provided" }

            val client = okHttpClient ?: OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .ssl(sslSocketFactory, trustManager)
                .build()

            return OkhttpWebSocketClient(client, url, headers)
        }
    }

    override fun connect(): Boolean {
        if (isConnected) return false

        val request = Request.Builder().url(url).apply {
            headers.forEach { addHeader(it.key, it.value) }
        }.build()

        webSocket = okHttpClient.newWebSocket(request, object : okhttp3.WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                listener?.onOpen()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                listener?.onMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                listener?.onMessage(bytes.toByteArray())
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                listener?.onClose(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                listener?.onFailure(t)
            }
        })
        return true
    }

    override fun disconnect() {
        webSocket?.close(1000, "Normal Closure")
        isConnected = false
    }

    override fun send(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }

    override fun send(message: ByteArray): Boolean {
        val byteString = message.toByteString()
        return webSocket?.send(byteString) == true
    }

    override fun setListener(listener: WebSocketListener) {
        this.listener = listener
    }

    override fun isConnected(): Boolean {
        return isConnected
    }

    override fun release() {
        listener = null
    }
}
