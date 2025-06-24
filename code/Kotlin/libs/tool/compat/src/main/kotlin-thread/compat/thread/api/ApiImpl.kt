package compat.thread.api

@Suppress("DEPRECATION")
class ApiImpl : Api {
    override fun threadId(thread: Thread): Long {
        return thread.id
    }
}