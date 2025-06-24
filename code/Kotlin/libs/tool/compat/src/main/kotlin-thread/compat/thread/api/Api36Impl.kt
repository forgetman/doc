package compat.thread.api

import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
class Api36Impl : Api by ApiImpl() {

    override fun threadId(thread: Thread): Long {
        return thread.threadId()
    }
}