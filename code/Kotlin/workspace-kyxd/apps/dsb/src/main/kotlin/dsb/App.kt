package dsb

import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dsb.model.City
import dsb.model.GpsCity
import eth.model.Nive
import lib.base.Sp
import lib.base.model.Page
import lib.base.model.User
import lib.sy.SY
import lib.udesk.UDesk
import lib.um.UM
import lib.um.share.UMShare
import lib.um.stats.UMStats
import logger.L
import logger.model.LoggerConfig
import vector.AppEx
import vector.app.appbar.AppBarConfig
import vector.config.AppConfig
import vector.config.ImageConfig
import vector.config.ListConfig
import vector.os.colorRes
import vector.app.os.dp
import vector.app.os.drawableRes
import vector.util.DeviceUtil
import vector.util.PackageUtil
import vector.util.Stats


/**
 * @author yuansui
 * @since 2019/1/17
 */
@HiltAndroidApp
class App : AppEx() {

    companion object {
        private const val KEY_CHANNEL_NAME = "channel_name"

        private const val KEY_UM = "583f9fb83eae2547ee000d27"

        var currCity: City? = null
            get() {
                return if (field == null) {
                    City().apply {
                        id = GpsCity.id
                        name = GpsCity.name
                    }
                } else field
            }
        var useGpsCity = true

        val unreadCount = Nive(0)
    }

    internal object AppBarVal {
        const val HEIGHT = 48
        const val ICON_SIZE = 27
        const val ICON_PADDING = 0
        const val TEXT_SIZE = 18
        const val TEXT_PADDING = 12
    }

    override fun onCreateInMainProcess() {
        super.onCreateInMainProcess()

        L.init(this, LoggerConfig.Initial.build {

        })
        L.setDebuggable(BuildConfig.DEBUG_LOG)

        DeviceUtil.closeStrictMode()

        initUM(this)
        initUDesk(this)
        initSY253(this)
    }

    override fun configureApp(): AppConfig = AppConfig.build {
        enableFlatBar = true
    }

    override fun configureImage(): ImageConfig = ImageConfig.build {
        cacheDir = Caching.imageCacheDir
    }

    override fun configureAppBar(): AppBarConfig = AppBarConfig.build {
        setLayout {
            height = AppBarVal.HEIGHT.dp
            background = R.color.nav_bar_bg.colorRes
        }
        setIcon {
            shape = AppBarConfig.Icon.Shape.SQUARE_INSIDE
            paddingStart = AppBarVal.ICON_PADDING.dp
            paddingEnd = AppBarVal.ICON_PADDING.dp
            size = AppBarVal.ICON_SIZE.dp
            background = R.drawable.selector_bg_nav_bar_focus.drawableRes
        }
        setText {
            textColor = R.color.nav_bar_text.colorRes
            paddingStart = AppBarVal.TEXT_PADDING.dp
            paddingEnd = AppBarVal.TEXT_PADDING.dp
            textSize = AppBarVal.TEXT_SIZE.dp
            background = R.drawable.nav_bar_shape_focus.drawableRes
        }
    }

    override fun configureList(): ListConfig = ListConfig.build {
        initOffset = Page.START
        limit = Page.LIMIT
    }

    private fun initUM(context: Context) {
        UM.init(context, KEY_UM, PackageUtil.getMetaValue(KEY_CHANNEL_NAME).orEmpty())

        UMShare.configWX("wx1d3b9fc68fde2d99", "9e6f1924360311002a028dc3ced07fa0")
        UMShare.configQZone("1105889298", "HO59f8FqlRPttTLh")

        // 初始化UM统计
        Stats.init(UMStats(), BuildConfig.DEBUG_LOG)
    }

    private fun initUDesk(context: Context) {
        UDesk.init(
            context,
            "dashebao.udesk.cn",
            "604b164f1aeadd6a818903dd0b4fc08d",
            "b92263829f2c7ea5"
        )

        if (Sp.isSignIn()) {
            val mobile = User.get().mobile
            UDesk.login(mobile, mobile)
        }
    }

    private fun initSY253(context: Context) {
        SY.config {
            debuggable = BuildConfig.DEBUG_LOG
            appId = if (debuggable) {
                "fHkSjYSk"
            } else {
                "O6CT05hS"
            }
            this.context = context
        }
    }
}