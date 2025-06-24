package xy.calendar

import lib.base.BaseApp
import vector.config.AppConfig
import vector.config.ImageConfig

class App : BaseApp() {

    override fun configureApp(): AppConfig = AppConfig.build {
        //        imageCacheDir = CacheUtil.getBmpCachePath()
    }

    override fun configureImage() = ImageConfig.build {
    }

    companion object {
//        val widgetInfo: WidgetInfo = WidgetInfo()
//
//        val mainActClass: Class<*>
//            get() = MonthActivity::class.ja va
//
//        var bgSwitcher: BgSwitcher? = null
//
//        val skinLoader: SkinLoader = SkinLoader()
//
//        fun setUseDefaultSkinState(state: Boolean) {
//            SpSetup.setUseDefaultSkinState(state)
//        }
//
//        val isUsingDefaultSkin: Boolean
//            get() = SpSetup.isUsingDefaultSkin
//
//
//        val isPortrait: Boolean
//            get() = SpSetup.isPortrait
    }
}
