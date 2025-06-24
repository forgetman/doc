package fund

import android.content.Context
import fund.util.Caching
import lib.um.UM
import lib.um.share.U_Share
import lib.um.stats.U_Stats
import logger.L
import vector.AppEx
import vector.config.AppConfig
import vector.config.ListConfig
import vector.config.NavBarConfig
import vector.stats.Stats
import vector.util.DeviceUtil


/**
 * @author yuansui
 * @since 2018/7/19
 */
class App : AppEx() {

    companion object {
        private const val UM_KEY = "5b7bcc09f29d982cae00012f"
    }

    override fun configureApp(): AppConfig = AppConfig.build {
        imageDiskName = Caching.BMP_CACHE_DISK_NAME
        enableFlatBar = false
        bgColorRes = R.color.app_bg
    }

    override fun configureNavBar(): NavBarConfig = NavBarConfig.build {
        height = 44
        bgColorRes = R.color.white
        textSize = 16
        focusBgDrawableRes = R.drawable.nav_bar_focus
        textColor = R.color.text_21
        textPadding = 15
        iconPadding = 10
        dividerHeight = 1
        dividerColorRes = R.color.app_bg
    }

    override fun configureList(): ListConfig = ListConfig.build {
        limit = 15
    }

    override fun onCreateInMainProcess() {
        super.onCreateInMainProcess()

        L.setDebug(BuildConfig.DEBUG_LOG)

        DeviceUtil.closeStrictMode()

        initUM(this)
    }

    private fun initUM(context: Context) {
        UM.init(context, UM_KEY, "office")

        U_Share.configWX("wx971bf35344c3a1fe", "948a30e2ec247e5247b85d6f4ec47b02")
        U_Share.configQZone("1106312982", "tPOvxanRfJlpNCYS")

        // 初始化UM统计
        Stats.init(U_Stats(), BuildConfig.DEBUG_LOG)
    }
}