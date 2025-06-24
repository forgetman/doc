package test.compose

import android.os.Environment
import androidx.compose.material3.darkColorScheme
import dagger.hilt.android.HiltAndroidApp
import logger.L
import logger.api.LoggerConfig
import logger.api.impl.LogcatOperator
import vector.app.ComposeAppEx
import vector.app.configuration.UiConfig
import vector.util.Dir

@HiltAndroidApp
class App : ComposeAppEx() {

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
        L.setDebuggable(true)
    }

    override fun configUi(): UiConfig {
        return UiConfig.build {
            colorScheme = darkColorScheme()
        }
    }
}