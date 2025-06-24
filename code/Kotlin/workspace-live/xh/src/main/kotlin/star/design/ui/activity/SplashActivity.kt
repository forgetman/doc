package star.design.ui.activity

import android.view.View
import star.R
import vector.app.activity.SplashActivityEx
import vector.ext.startActivity
import vector.app.util.inflate

/**
 * @author yuansui
 * @since 2020-04-10
 */
class SplashActivity : SplashActivityEx() {

    override fun createContentView(): View {
        return R.layout.activity_splash.inflate(this)
    }

    override fun passTo() {
        startActivity<MainActivity>()
    }
}