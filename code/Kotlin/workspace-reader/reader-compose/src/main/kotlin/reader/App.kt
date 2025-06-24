package reader

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import dagger.hilt.android.HiltAndroidApp
import logger.L
import logger.api.LoggerConfig
import logger.api.impl.LogcatOperator
import reader.ui.theme.scheme
import vector.app.ComposeAppEx
import vector.app.configuration.UiConfig
import vector.ext.killAndExitProcess
import vector.util.DeviceUtil
import vector.util.Dir

/**
 * @author yuansui
 * @since 2017/6/26
 */
@HiltAndroidApp
class App : ComposeAppEx() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        // 编译器无法分辨context的keeper主体是否为Application本身
        lateinit var context: Context
    }

    private val Dir.log: String
        get() = mkFilesDir(Environment.DIRECTORY_DOCUMENTS, "log")

    override fun onCreate() {
        super.onCreate()

        L.setConfig(LoggerConfig(this) {
            setCachePath(Dir.log)
            setVersionName(BuildConfig.VERSION_NAME)
            setVersionCode(BuildConfig.VERSION_CODE.toString())
            setLevel(LoggerConfig.Level.VERBOSE)
        })
        L.setOperator(LogcatOperator())
        L.setTraceable(true)

        DeviceUtil.closeStrictMode()

        catchException()

        // TODO: 不确定是否还需要在此同步设置夜间模式
//        setDayNightMode(Pref.dayNightMode)
    }

    private fun catchException() {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            L.e(e)
            killAndExitProcess()
        }
    }

    override fun configUi(): UiConfig {
        return UiConfig.build {
            colorScheme = scheme()
        }
    }
}
