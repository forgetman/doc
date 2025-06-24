package eos.api.model

sealed class Message {
    data class TextMessage(val content: String) : Message()
    data class BinaryMessage(val content: ByteArray) : Message()
}