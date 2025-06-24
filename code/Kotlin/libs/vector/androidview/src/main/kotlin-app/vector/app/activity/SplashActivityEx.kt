package vector.app.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import sugar.ext.runOnMainThread
import java.util.concurrent.TimeUnit

/**
 * 封装了延迟跳转机制的闪屏基类
 */
@SuppressLint("CustomSplashScreen")
abstract class SplashActivityEx : SimpleActivityEx() {

    /**
     * @return 毫秒
     */
    open val pastDelayMillis: Long
        get() = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun initializeContentView() {
        doOnPreDraw {
            doPass()
        }
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

    /**
     * splash显示完以后开启下一个跳转的activity, 然后会自动finish此activity
     */
    abstract fun passTo()
}
