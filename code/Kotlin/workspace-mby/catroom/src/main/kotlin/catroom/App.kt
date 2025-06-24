package catroom

import android.graphics.Color
import android.os.Environment
import dagger.hilt.android.HiltAndroidApp
import logger.L
import logger.api.Config
import logger.api.LoggerConfig
import logger.api.impl.XlogOperator
import vector.AppEx
import vector.app.appbar.AppBarConfig
import vector.config.AppConfig
import vector.config.FitConfig
import vector.config.ImageConfig
import vector.os.colorInt
import vector.app.os.dp
import vector.util.Dir
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class App : AppEx() {

    companion object {
        private const val LOG_DIR_NAME = "log"
    }

    private val Dir.image: String
        get() = mkCacheDir("bmp")

    override fun configureApp(): AppConfig = AppConfig.build {
        enableFlatBar = true
    }

    override fun configureImage(): ImageConfig = ImageConfig.build {
        cacheDir = Dir.image
    }

    override fun configureAppBar(): AppBarConfig = AppBarConfig.build {
        setLayout {
            height = 20.dp
            background = Color.BLACK.colorInt
        }
        setIcon {
            shape = AppBarConfig.Icon.Shape.WRAP
            groupMarginStart = 10.dp
            groupMarginEnd = 10.dp
        }
        setText {
            textSize = 10.dp
            textColor = Color.WHITE.colorInt
            paddingStart = 5.dp
            paddingEnd = 5.dp
            drawablePadding = 5.dp
        }
    }

    override fun configureFit(): FitConfig {
        return FitConfig.build {
            density = 2f
            width = 1280f
            height = 720f
        }
    }

    override fun onCreateInMainProcess() {
        initXlog()
    }

    private fun initXlog() {
        val logPath = Dir.mkFilesDir(Environment.DIRECTORY_DOCUMENTS, LOG_DIR_NAME)

        val config = LoggerConfig(this) {
            setCachePath(logPath)
            setVersionName(BuildConfig.VERSION_NAME)
            setVersionCode(BuildConfig.VERSION_CODE.toString())
            setLevel(Config.Level.VERBOSE) // 目前打印全log
            setPrefix("catroom") // log前缀(traceable为false时起作用)
            setCacheTimeoutMillis(TimeUnit.DAYS.toMillis(3)) // 最多保存3天log(节省流量)
        }
        L.setConfig(config)
        L.setOperator(XlogOperator(config))
        L.setUploader(XlogUploader(logPath))
        L.setTraceable(BuildConfig.DEBUG_LOG)
        L.setDebuggable(true) // release也开启logcat输出
    }
}