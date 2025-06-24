package test

import android.os.Environment
import logger.L
import logger.api.LoggerConfig
import logger.api.impl.LogcatOperator
import vector.AppTestEx
import vector.app.config.AppConfig
import vector.util.Dir

/**
 * @author yuansui
 * @since 2018/6/4
 */
class App : AppTestEx() {

    private val Dir.log: String
        get() = mkFilesDir(Environment.DIRECTORY_DOCUMENTS, "log")

    override fun onCreateInAllProcess() {
        L.setConfig(LoggerConfig(this) {
            setCachePath(Dir.log)
            setVersionName(BuildConfig.VERSION_NAME)
            setVersionCode(BuildConfig.VERSION_CODE.toString())
            setLevel(LoggerConfig.Level.VERBOSE)
        })
        L.setOperator(LogcatOperator())
        L.setTraceable(true)
        L.setDebuggable(true)
    }

    override fun configureApp(): AppConfig = AppConfig.build {
        enableFlatBar = true
    }

    override fun shouldInitializeConfigInMultiProcess(): Boolean {
        return true
    }
}