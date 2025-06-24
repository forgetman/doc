package dsb.design.ui.activity

import android.view.View
import dsb.SpApp
import dsb.databinding.ActivitySplashBinding
import dsb.model.GpsCity
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import lib.base.Sp
import lib.base.serv.CommonService
import lib.base.serv.CommonServiceCreator
import vector.app.activity.SplashActivityEx
import vector.ext.enterFullScreen
import vector.ext.startActivity

/**
 * @author yuansui
 * @since 2019/1/17
 */
@Creator
class SplashActivity : SplashActivityEx() {

    @Extra
    var pushMessage: String? = null

    override val pastDelayMillis: Long
        get() = 1500


    override fun createContentView(): View {
        return ActivitySplashBinding.inflate(layoutInflater).root
    }

    override fun flowOfSetup() {
        super.flowOfSetup()

        enterFullScreen()
    }

    override fun passTo() {
        if (Sp.getDid().isNullOrEmpty()) {
            CommonServiceCreator.create(CommonService.Type.DEVICE_ID).start(this)
        }

        GpsCity.reset()

        if (!SpApp.getBoolean(SpApp.SHOW_NEW_TIP_ON_230, false)) {
            startActivity<NewTipActivity>()
        } else {
            MainActivityCreator.create().pushMessage(pushMessage).start(this)
        }
    }
}