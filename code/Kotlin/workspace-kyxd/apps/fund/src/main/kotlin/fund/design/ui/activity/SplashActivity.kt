package fund.design.ui.activity

import androidx.databinding.ViewDataBinding
import fund.databinding.ActivitySplashBinding
import fund.service.CommonService
import fund.service.CommonServiceCreator
import lib.base.Sp
import vector.design.ui.activity.SplashActivityEx
import vector.ext.startActivity

/**
 * @author yuansui
 * @since 2018/7/10
 */
class SplashActivity : SplashActivityEx() {

    override val pastDelay: Long
        get() = 1500

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun initializeData() {
        if (Sp.getDid().isNullOrEmpty()) {
            CommonServiceCreator.create(CommonService.Type.DEVICE_ID).start(this)
        }
    }

    override fun passTo() {
        startActivity(MainActivity::class)
    }
}