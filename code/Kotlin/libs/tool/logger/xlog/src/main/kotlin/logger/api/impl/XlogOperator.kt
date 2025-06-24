package logger.api.impl

import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import logger.api.LoggerConfig
import logger.api.Operator
import java.util.concurrent.TimeUnit

class XlogOperator(config: LoggerConfig) : Operator {

    init {
        System.loadLibrary("c++_shared")
        System.loadLibrary("marsxlog")

        val xlog = Xlog()
        xlog.setMaxFileSize(0, 5 * 1024 * 1024)
        config.cacheTimeoutMillis()?.let { millis ->
            xlog.setMaxAliveTime(0, TimeUnit.MILLISECONDS.toSeconds(millis))
        } ?: run {
            xlog.setMaxAliveTime(0, TimeUnit.DAYS.toSeconds(3))
        }

        Log.setLogImp(xlog)
        Log.setConsoleLogOpen(true)
        Log.appenderOpen(
            when (config.level()) {
                LoggerConfig.Level.VERBOSE -> Xlog.LEVEL_VERBOSE
                LoggerConfig.Level.DEBUG -> Xlog.LEVEL_DEBUG
                LoggerConfig.Level.INFO -> Xlog.LEVEL_INFO
                LoggerConfig.Level.WARN -> Xlog.LEVEL_WARNING
                LoggerConfig.Level.ERROR -> Xlog.LEVEL_ERROR
            },
            Xlog.AppednerModeAsync,
            "",
            config.cachePath(),
            "",
            0
        )
    }

    override fun print(
        level: LoggerConfig.Level,
        tag: String?,
        message: String
    ) {
        when (level) {
            LoggerConfig.Level.VERBOSE -> Log.v(tag, message)
            LoggerConfig.Level.DEBUG -> Log.d(tag, message)
            LoggerConfig.Level.INFO -> Log.i(tag, message)
            LoggerConfig.Level.WARN -> Log.w(tag, message)
            LoggerConfig.Level.ERROR -> Log.e(tag, message)
        }
    }

    override fun setDebuggable(debuggable: Boolean) {
        Log.setConsoleLogOpen(debuggable)
    }

    override fun flushToFile() {
        Log.appenderFlush()
    }
}