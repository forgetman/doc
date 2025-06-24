package star

import android.os.Environment
import logger.L
import logger.api.Config
import logger.api.LoggerConfig
import logger.api.impl.LogcatOperator
import vector.AppTestEx
import vector.util.Dir

/**
 * @author yuansui
 * @since 2018/6/4
 */
class App : AppTestEx() {

    private val Dir.log: String
        get() = mkFilesDir(Environment.DIRECTORY_DOCUMENTS, "log")

    override fun onCreateInMainProcess() {
        super.onCreateInMainProcess()

        L.setConfig(LoggerConfig(this) {
            setCachePath(Dir.log)
            setVersionName(BuildConfig.VERSION_NAME)
            setVersionCode(BuildConfig.VERSION_CODE.toString())
            setLevel(Config.Level.VERBOSE)
        })
        L.setOperator(LogcatOperator())
        L.setTraceable(true)
        L.setDebuggable(true)
    }
}