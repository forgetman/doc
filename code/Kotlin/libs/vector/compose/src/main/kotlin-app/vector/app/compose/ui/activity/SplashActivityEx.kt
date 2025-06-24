package vector.app.compose.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import sugar.ext.runOnMainThread
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
abstract class SplashActivityEx : SimpleComposeActivityEx() {

    open val pastDelayMillis: Long
        get() = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        super.onCreate(savedInstanceState)
        doPass()
    }

    private fun setupSplashScreen() {
        val splashScreen = installSplashScreen()
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            splashScreenViewProvider.remove()
        }
    }

    private fun doPass() {
        runOnMainThread(pastDelayMillis, TimeUnit.MILLISECONDS, this) {
            if (!isFinishing) passTo()
            finish()
        }
    }

    abstract fun passTo()
}