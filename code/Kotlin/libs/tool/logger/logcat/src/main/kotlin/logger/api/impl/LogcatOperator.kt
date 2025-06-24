package logger.api.impl

import android.util.Log
import logger.api.LoggerConfig
import logger.api.Operator

class LogcatOperator : Operator {

    override fun print(level: LoggerConfig.Level, tag: String?, message: String) {
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

    override fun setDebuggable(debuggable: Boolean) {
        // do nothing
    }

    override fun flushToFile() {
        // do nothing
    }
}