package logger.api

import android.content.Context

interface LoggerConfig {
    enum class Level(val type: Int) {
        VERBOSE(1),
        DEBUG(2),
        INFO(3),
        WARN(4),
        ERROR(5)
    }

    fun cachePath(): String
    fun versionName(): String
    fun versionCode(): String

    /**
     * log打印的最低等级要求
     */
    fun level(): Level

    /**
     *  log打印tag的统一前缀
     */
    fun prefix(): String
    fun cacheTimeoutMillis(): Long?
}

interface ConfigBuilder {
    fun setCachePath(cachePath: String)
    fun setVersionName(versionName: String)
    fun setVersionCode(versionCode: String)
    fun setLevel(level: LoggerConfig.Level)
    fun setPrefix(prefix: String)
    fun setCacheTimeoutMillis(cacheTimeoutMillis: Long?)
    fun build(): LoggerConfig
}

fun LoggerConfig(context: Context, builderAction: ConfigBuilder.() -> Unit): LoggerConfig {
    val builder = ConfigBuilderImpl(context)
    builderAction(builder)
    return builder.build()
}

private class ConfigBuilderImpl(private val context: Context) : ConfigBuilder {
    private var cachePath: String = ""
    private var versionName: String = ""
    private var versionCode: String = ""
    private var level: LoggerConfig.Level = LoggerConfig.Level.VERBOSE
    private var prefix: String = ""
    private var cacheTimeoutMillis: Long? = null

    override fun setCachePath(cachePath: String) {
        this.cachePath = cachePath
    }

    override fun setVersionName(versionName: String) {
        this.versionName = versionName
    }

    override fun setVersionCode(versionCode: String) {
        this.versionCode = versionCode
    }

    override fun setLevel(level: LoggerConfig.Level) {
        this.level = level
    }

    override fun setPrefix(prefix: String) {
        this.prefix = prefix
    }

    override fun setCacheTimeoutMillis(cacheTimeoutMillis: Long?) {
        this.cacheTimeoutMillis = cacheTimeoutMillis
    }

    override fun build(): LoggerConfig {
        return object : LoggerConfig {
            override fun cachePath(): String = cachePath
            override fun versionName(): String = versionName
            override fun versionCode(): String = versionCode
            override fun level(): LoggerConfig.Level = level
            override fun prefix(): String = prefix
            override fun cacheTimeoutMillis(): Long? = cacheTimeoutMillis
        }
    }
}