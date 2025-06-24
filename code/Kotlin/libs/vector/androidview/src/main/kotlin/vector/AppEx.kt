package vector

import android.content.Context
import androidx.multidex.MultiDexApplication
import vector.app.appbar.AppBarConfig
import vector.app.config.AppConfig
import vector.app.config.Config
import vector.app.config.FitConfig
import vector.app.config.ImageConfig
import vector.app.config.ListConfig
import vector.app.config.ViewPagerConfig
import vector.ext.isMainProcess
import vector.app.fitter.Fitter
import vector.util.IMMLeaks

/**
 * @author yuansui
 * @since 2018/2/6
 */
abstract class AppEx : CoreAppEx() {

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        onPreCreate()

        fun initializeConfigs() {
            Config.Initializer.forApp(configureApp())
            Config.Initializer.forList(configureList())
            Config.Initializer.forImage(configureImage())
            Config.Initializer.forFit(configureFit())
            Config.Initializer.forAppBar(configureAppBar())
            Config.Initializer.forViewPager(configureViewPager())

            Fitter.register(this)

            IMMLeaks.fixFocusedViewLeak(this)
        }

        if (shouldInitializeConfigInMultiProcess()) {
            initializeConfigs()
            if (isMainProcess()) {
                onCreateInMainProcess()
            } else {
                onCreateInChildProcess()
            }
        } else {
            if (isMainProcess()) {
                initializeConfigs()
                onCreateInMainProcess()
            } else {
                onCreateInChildProcess()
            }
        }

        onCreateInAllProcess()
    }

    /**
     * [Config]是否在多进程环境也需要进行初始化
     * 大部分情况下, 子进程不需要UI绘制
     */
    open fun shouldInitializeConfigInMultiProcess(): Boolean {
        return false
    }

    /**
     * 在初始化之前做一些事情, 比如设置ApplicationContext
     */
    open fun onPreCreate() {}

    /**
     * 在主进程里初始化
     */
    open fun onCreateInMainProcess() {}

    /**
     * 在子进程里初始化
     */
    open fun onCreateInChildProcess() {}

    /**
     * 在所有进程里初始化
     */
    open fun onCreateInAllProcess() {}

    /**
     * 设置App
     */
    abstract fun configureApp(): AppConfig

    abstract fun configureImage(): ImageConfig

    /**
     * 设置[vector.widget.scrollable.ListView]参数, 使用默认参数
     */
    open fun configureList(): ListConfig = ListConfig.build { }

    /**
     * 设置[vector.widget.viewpager2.ViewPager2]参数, 使用默认参数
     */
    open fun configureViewPager(): ViewPagerConfig = ViewPagerConfig.build { }

    /**
     * 设置屏幕适配参数, 使用默认参数
     */
    open fun configureFit(): FitConfig = FitConfig.build { }

    /**
     * 设置[AppBarConfig]
     */
    abstract fun configureAppBar(): AppBarConfig
}

/**
 * 内部使用的全局applicationContext
 */
internal lateinit var appContext: Context