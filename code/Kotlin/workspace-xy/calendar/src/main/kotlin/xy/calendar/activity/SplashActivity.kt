package xy.calendar.activity

import android.view.View
import vector.app.activity.SplashActivityEx
import vector.ext.startActivity
import vector.app.util.inflate
import xy.calendar.R

class SplashActivity : SplashActivityEx() {

    override fun createContentView(): View {
        return R.layout.activity_splash.inflate(this)
    }

    override fun passTo() {
        startActivity<MainActivity>()
    }

}
