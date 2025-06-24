package logger.api


@Suppress("unused")
interface Operator {
    /**
     * 输出日志
     * @param level 日志级别(verbose,info,debug,warn,error)
     * @param tag 日志tag
     * @param message 日志内容
     */
    fun print(level: LoggerConfig.Level, tag: String?, message: String)

    fun setDebuggable(debuggable: Boolean)

    /**
     * 将缓存的日志刷写到文件
     */
    fun flushToFile()
}
