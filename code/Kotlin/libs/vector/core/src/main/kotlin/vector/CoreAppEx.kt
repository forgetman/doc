package vector

import android.content.Context
import androidx.multidex.MultiDexApplication

/**
 * @author yuansui
 * @since 2018/2/6
 */
abstract class CoreAppEx : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
    }
}

/**
 * 内部使用的全局applicationContext
 */
internal lateinit var appContext: Context