package pretimmediat

import dagger.hilt.android.HiltAndroidApp
import logger.L
import logger.api.LoggerConfig
import logger.api.impl.LogcatOperator
import pretimmediat.manager.GaidManager
import pretimmediat.manager.IpManager
import pretimmediat.property.Properties
import pretimmediat.stats.Stats
import sugar.ext.runOnSubThread
import vector.AppEx
import vector.app.appbar.AppBarConfig
import vector.app.config.AppConfig
import vector.app.config.ImageConfig
import vector.app.os.colorRes
import vector.app.os.dp
import vector.util.Dir


@HiltAndroidApp
class App : AppEx() {

    companion object {
        private const val LOG_TAG = "App"
    }

    private val Dir.image: String
        get() = mkCacheDir("bmp")

    override fun configureApp(): AppConfig = AppConfig.build {
        enableFlatBar = true
    }

    override fun configureImage(): ImageConfig = ImageConfig.build {
        cacheDir = Dir.image
        defaultError = R.drawable.layer_ic_error
    }

    override fun configureAppBar(): AppBarConfig = AppBarConfig.build {
        setLayout {
            height = 42.dp
        }
        setIcon {
            shape = AppBarConfig.Icon.Shape.WRAP
            groupMarginStart = 19.dp
            groupMarginEnd = 19.dp
        }
        setText {
            textSize = 14.dp
            textColor = R.color.text_appbar_primary.colorRes
            paddingStart = 19.dp
            paddingEnd = 19.dp
            drawablePadding = 10.dp
        }
    }

    override fun onCreateInMainProcess() {
        initLog()

        runOnSubThread {
            try {
                val adid = GaidManager.getGoogleAdId(this@App) ?: return@runOnSubThread
                L.d(LOG_TAG, "getGoogleAdId: $adid")
                Properties.gaid.put(adid)
            } catch (e: Exception) {
                L.e(LOG_TAG, "getGoogleAdId", e)
            }

            IpManager.getInstance(this@App).getAddress()?.let {
                L.d(LOG_TAG, "onCreateInMainProcess, ip: $it")
                Properties.ip.put(it)
            }
        }
    }

    override fun onCreateInAllProcess() {
        Stats.init(this)
    }

    private fun initLog() {
        L.setConfig(
            LoggerConfig(this) {
                setVersionName(BuildConfig.VERSION_NAME)
                setVersionCode(BuildConfig.VERSION_CODE.toString())
                setLevel(LoggerConfig.Level.VERBOSE)
            }
        )
        L.setOperator(LogcatOperator())
        L.setTraceable(BuildConfig.DEBUG_LOG)
    }
}