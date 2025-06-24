package compat.thread

import compat.thread.api.Api
import compat.thread.api.Api36Impl
import compat.thread.api.ApiImpl
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast

object ThreadCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.B_36) -> Api36Impl()
        else -> ApiImpl()
    }

    fun threadId(thread: Thread): Long = api.threadId(thread)
}