package logger

import android.os.Build
import android.os.Process
import logger.api.LoggerConfig
import logger.api.Operator
import logger.api.Uploader
import logger.ext.getStackTraceString
import java.util.Date

@Suppress("unused")
abstract class Logger {

    companion object {
        private const val TAG_WWW = "www"
        private const val TAG_TEMP = "temp_tag"
        private const val TOP_LEFT_CORNER = "┌"
        private const val BOTTOM_LEFT_CORNER = '└'
        private const val HORIZONTAL_LINE = '|'
        private const val DOUBLE_DIVIDER = "──────────────────────────────"
        private const val TOP_BORDER = "$TOP_LEFT_CORNER$DOUBLE_DIVIDER$DOUBLE_DIVIDER"
        private const val BOTTOM_BORDER = "$BOTTOM_LEFT_CORNER$DOUBLE_DIVIDER$DOUBLE_DIVIDER"
        private const val CHUNK_SIZE = 4000
    }

    private val groupClassName = GroupByBuilder::class.java.name

    protected abstract val loggerClassName: String

    private lateinit var config: LoggerConfig
    private lateinit var operator: Operator
    private var uploader: Uploader? = null

    private var traceable: Boolean = true // 是否打印调用栈
    private var debuggable: Boolean = true // 是否打印log

    fun setConfig(config: LoggerConfig) {
        this.config = config
    }

    fun setOperator(operator: Operator) {
        this.operator = operator
    }

    fun setUploader(uploader: Uploader) {
        this.uploader = uploader
    }

    /**
     * @param traceable true会打印调用栈, false不打印
     */
    fun setTraceable(traceable: Boolean) {
        this.traceable = traceable
    }

    /**
     * @param debuggable true会打印到logcat, false则不会
     */
    fun setDebuggable(debuggable: Boolean) {
        this.debuggable = debuggable
        operator.setDebuggable(debuggable)
    }

    private fun doLog(level: LoggerConfig.Level, tag: String?, msg: String?, trace: Boolean) {
        if (!debuggable || msg.isNullOrEmpty()) return
        val traceElement = if (trace) getTraceElement() else null
        if (traceElement != null) {
            val traceString = buildString {
                append("(")
                append(traceElement.fileName)
                append(":")
                append(traceElement.lineNumber)
                appendLine(")")
            }
            printLog(level, tag, traceString)
        }

        val showBorder = trace && traceable
        val formattedMsg = buildString {
            if (showBorder) append(TOP_BORDER).appendLine()

            if (showBorder) {
                // 处理消息内容，逐行添加 HORIZONTAL_LINE
                msg.lines().forEach { line ->
                    append(HORIZONTAL_LINE).append(" ").append(line).appendLine()
                }
            } else {
                append(msg)
            }

            if (showBorder) append(BOTTOM_BORDER)
        }

        printLog(level, tag, formattedMsg)
    }

    private fun printLog(level: LoggerConfig.Level, tag: String?, msg: String) {
        msg.chunked(CHUNK_SIZE).forEach {
            operator.print(level, tag, it)
        }
    }

    private fun getTraceElement(): StackTraceElement? {
        if (traceable.not()) return null
        // find the target invoked method
        var shouldTrace = false
        Thread.currentThread().stackTrace.forEach { element ->
            val className = element.className
            val isLogMethod = className == loggerClassName || className == groupClassName
            if (shouldTrace && !isLogMethod) {
                return element
            }
            shouldTrace = isLogMethod
        }
        return null
    }

    // 记录线程信息
    fun trace() {
        val thread = Thread.currentThread()
        val threadMsg = buildString {
            append("Process_id: ${Process.myPid()}").appendLine()
            append("Thread_name: ${thread.name}").appendLine()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                append("Thread_tid: ${thread.threadId()}")
            } else {
                @Suppress("DEPRECATION")
                append("Thread_id: ${thread.id}")
            }
        }
        d(msg = threadMsg)
    }

    // 日志级别方法
    fun d(msg: String?) {
        d(TAG_TEMP, msg)
    }

    fun d(tag: String?, msg: String?) = doLog(LoggerConfig.Level.DEBUG, tag, msg, true)

    fun i(tag: String? = TAG_TEMP, msg: String?) = doLog(LoggerConfig.Level.INFO, tag, msg, true)

    fun e(msg: String?) = e(TAG_TEMP, msg)
    fun e(throwable: Throwable) = e(TAG_TEMP, throwable)
    fun e(tag: String?, msg: String?) = doLog(LoggerConfig.Level.ERROR, tag, msg, true)
    fun e(tag: String?, throwable: Throwable) {
        val stackTrace = throwable.getStackTraceString()
        doLog(LoggerConfig.Level.ERROR, tag, stackTrace, true)
    }

    fun e(tag: String?, msg: String?, throwable: Throwable) {
        val combinedMessage = buildString {
            append(msg ?: "An error occurred")
            appendLine()
            append(throwable.getStackTraceString())
        }
        doLog(LoggerConfig.Level.ERROR, tag, combinedMessage, true)
    }

    fun w(tag: String? = TAG_TEMP, msg: String?) = doLog(LoggerConfig.Level.WARN, tag, msg, true)
    fun v(tag: String? = TAG_TEMP, msg: String?) = doLog(LoggerConfig.Level.VERBOSE, tag, msg, true)

    fun www(msg: String?) = doLog(LoggerConfig.Level.DEBUG, TAG_WWW, msg, false)

    fun upload(date: Date? = null, result: Uploader.ResultListener? = null) {
        uploader?.upload(date, result)
    }

    fun saveToFile() {
        operator.flushToFile()
    }

    fun uploadAll(result: Uploader.ResultListener? = null) {
        uploader?.uploadAll(result)
    }

    private fun getTag(element: StackTraceElement?): String {
        var result: String

        val fileName = element?.fileName
        if (element != null && !fileName.isNullOrEmpty()) {
            result = fileName
            if (result.contains(".")) {
                val lastIndex = result.lastIndexOf(".")
                result = result.substring(0, lastIndex)
            }
        } else {
            result = TAG_TEMP
        }
        return prefixTag(result)
    }

    private fun formatTag(tag: String?): String {
        return if (tag == null) {
            getTag(getTraceElement())
        } else {
            prefixTag(tag)
        }
    }

    private fun prefixTag(tempTag: String): String {
        return if (traceable.not() && config.prefix().isNotEmpty()) {
            if (tempTag == TAG_TEMP) {
                config.prefix()
            } else {
                // 暂时直接添加前缀, 不用tempTag.startsWith()判断, 减少消耗
                "${config.prefix()}_$tempTag"
            }
        } else {
            tempTag
        }
    }

    fun groupBy(block: GroupByBuilder.() -> Unit): GroupByBuilder {
        val builder = GroupByBuilder(formatTag(null))
        block(builder)
        return builder
    }

    fun groupBy(vararg messages: String): GroupByBuilder {
        val builder = GroupByBuilder(formatTag(null))
        messages.forEach {
            builder.append(it)
        }
        return builder
    }

    inner class GroupByBuilder(private var tag: String?) {
        private var type = LoggerConfig.Level.VERBOSE
        private val stringBuilder = StringBuilder()

        fun append(msg: String?) = apply { stringBuilder.append(msg).appendLine() }

        // 输出日志
        fun d(tag: String? = null) = log(LoggerConfig.Level.DEBUG, tag)
        fun v(tag: String? = null) = log(LoggerConfig.Level.VERBOSE, tag)
        fun i(tag: String? = null) = log(LoggerConfig.Level.INFO, tag)
        fun e(tag: String? = null) = log(LoggerConfig.Level.ERROR, tag)
        fun w(tag: String? = null) = log(LoggerConfig.Level.WARN, tag)

        private fun log(level: LoggerConfig.Level, tag: String?) {
            doLog(level, tag ?: this.tag, stringBuilder.toString(), true)
        }
    }
}