package compat.thread.api

/**
 * @author yuansui
 * @since 2025/5/5
 */
interface Api {
    fun threadId(thread: Thread): Long
}