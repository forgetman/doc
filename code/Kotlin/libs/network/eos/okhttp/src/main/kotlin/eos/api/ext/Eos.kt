package eos.api.ext

import eos.api.Eos
import eos.api.impl.OkhttpWebSocketClient

fun Eos.Companion.default(baseUrl: String): Eos {
    val client = OkhttpWebSocketClient.Builder().url(baseUrl).build()
    return Eos(client)
}