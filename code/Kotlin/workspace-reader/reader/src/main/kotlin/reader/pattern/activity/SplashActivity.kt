package reader.pattern.activity

import android.app.Activity
import android.view.View
import reader.R
import reader.databinding.ActivitySplashBinding
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.app.activity.SplashActivityEx
import vector.app.ext.enterFullScreen
import vector.ext.startActivity

/**
 * @author yuansui
 * @since 2017/6/27
 */
class SplashActivity : SplashActivityEx() {

    override val pastDelayMillis: Long
        get() = 2000

    override fun createContentView(): View {
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enterFullScreen()
        }
    }

    override fun passTo() {
        startActivity<MainActivity>()
    }

    override fun finish() {
        super.finish()

        if (isSdkAtLeast(SdkInt.U_34)) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.hold)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.fade_in, R.anim.hold)
        }
    }
}
