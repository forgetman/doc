package logger.api.impl

import android.content.Context
import android.util.Log
import com.dianping.logan.Logan
import com.dianping.logan.LoganConfig
import logger.L
import logger.api.LoggerConfig
import logger.api.Operator

class LoganOperator(
    context: Context,
    config: LoggerConfig,
    encryptKey: String?,
    encryptIv: String?,
) : Operator {
    companion object {
        private const val LOG_TAG = "LoganOperator"
    }


    private var loganUseful: Boolean = false
    private var debuggable: Boolean = false


    init {
        val loganConfig = LoganConfig.Builder()
            .setCachePath(context.filesDir.absolutePath)
            .setPath(config.cachePath())
            .setEncryptKey16(encryptKey?.toByteArray())
            .setEncryptIV16(encryptIv?.toByteArray())
            .build()
        try {
            Logan.init(loganConfig)
            loganUseful = true
        } catch (e: Exception) {
            L.e(LOG_TAG, e)
        }
    }

    override fun print(level: LoggerConfig.Level, tag: String?, message: String) {
        // debuggable为true才输出到logcat, 不然只输出到logan文件里
        if (debuggable) {
            when (level) {
                LoggerConfig.Level.VERBOSE -> {
                    Log.v(tag, message)
                }

                LoggerConfig.Level.INFO -> {
                    Log.i(tag, message)
                }

                LoggerConfig.Level.DEBUG -> {
                    Log.d(tag, message)
                }

                LoggerConfig.Level.WARN -> {
                    Log.w(tag, message)
                }

                LoggerConfig.Level.ERROR -> {
                    Log.e(tag, message)
                }
            }
        }

        if (!loganUseful) return
        Logan.w("$tag $message", level.type)
    }

    override fun setDebuggable(debuggable: Boolean) {
        this.debuggable = debuggable
    }

    override fun flushToFile() {
        // do nothing
    }
}