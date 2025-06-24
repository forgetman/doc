package reader

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import dagger.hilt.android.HiltAndroidApp
import logger.L
import logger.api.LoggerConfig
import logger.api.impl.LogcatOperator
import reader.model.Page
import vector.AppEx
import vector.app.appbar.AppBarConfig
import vector.app.config.AppConfig
import vector.app.config.ImageConfig
import vector.app.config.ListConfig
import vector.app.os.colorRes
import vector.app.os.dp
import vector.app.os.drawableRes
import vector.ext.killAndExitProcess
import vector.util.DeviceUtil
import vector.util.Dir

/**
 * @author yuansui
 * @since 2017/6/26
 */
@HiltAndroidApp
class App : AppEx() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        // 编译器无法分辨context的keeper主体是否为Application本身
        lateinit var context: Context
    }

    private val Dir.log: String
        get() = mkFilesDir(Environment.DIRECTORY_DOCUMENTS, "log")

    override fun onCreateInAllProcess() {
        context = applicationContext
    }

    override fun onCreateInMainProcess() {
        super.onCreateInMainProcess()

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

        registerActivityLifecycleCallbacks(ActivityLifecycleCallbackImpl())

        // TODO: 不确定是否还需要在此同步设置夜间模式
//        setDayNightMode(Pref.dayNightMode)
    }

    private fun catchException() {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            L.e(e)
            killAndExitProcess()
        }
    }

    override fun configureApp(): AppConfig = AppConfig.build {
        enableFlatBar = true
    }

    override fun configureImage(): ImageConfig = ImageConfig.build {
        cacheDir = Dir.image
        defaultError = R.drawable.layer_ic_placeholder
    }

    override fun configureAppBar(): AppBarConfig = AppBarConfig.build {
        setLayout {
            height = 48.dp
        }
        setIcon {
            shape = AppBarConfig.Icon.Shape.WRAP
            paddingStart = 10.dp
            paddingEnd = 10.dp
            size = 16.dp
            groupMarginStart = 10.dp
            background = R.drawable.selector_nav_bar.drawableRes
        }
        setText {
            textSize = 16.dp
            textColor = R.color.selector_nav_bar_text.colorRes
            paddingStart = 10.dp
            paddingEnd = 10.dp
            background = R.drawable.selector_nav_bar_shape.drawableRes
        }
    }

    override fun configureList(): ListConfig = ListConfig.build {
        initOffset = Page.START
        limit = Page.LIMIT
    }
}
