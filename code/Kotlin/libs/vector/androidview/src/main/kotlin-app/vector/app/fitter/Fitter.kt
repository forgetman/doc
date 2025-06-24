package vector.app.fitter

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.collection.ArrayMap
import sugar.ext.SdkInt
import sugar.ext.isSdkAtMost
import vector.app.config.Config
import java.util.WeakHashMap

/**
 * @author yuansui
 * @since 2018/2/18 0018
 */
object Fitter {
    // 原始信息
    private val appMetrics = DisplayMetrics()

    // 持有原始比例的res, 以便读取本身的一些资源, 比如 状态栏高度 等
    lateinit var appResources: Resources

    private val metricsCache = ArrayMap<Mode, DisplayMetrics>()

    // Activity 的生命周期监测
    private var callback: ActivityLifecycleCallbackImpl? = null

    internal fun register(application: Application) {
        // 记录系统的原始值
        appResources = application.resources
        appMetrics.setTo(appResources.displayMetrics)

        // 添加字体变化的监听
        application.registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                // 字体改变后,将 scaledDensity 重新赋值
                // ps: 暂时没有用到scaledDensity来计算, 所以这次重新运算等于白做
                if (newConfig.fontScale > 0) {
                    appResources = application.resources
                    appMetrics.setTo(appResources.displayMetrics)

                    // 重新计算
                    metricsCache.clear()
                    FitResources.clear()
                }
            }

            override fun onLowMemory() {}
        })

        if (callback == null) {
            callback = ActivityLifecycleCallbackImpl()
            application.registerActivityLifecycleCallbacks(callback)
        }
    }

    fun fit(context: Context, mode: Mode = Config.fit().mode) {
        fit(context.resources, mode)
    }

    fun fit(res: Resources, mode: Mode) {
        fit(res.displayMetrics, mode)
    }

    fun fit(displayMetrics: DisplayMetrics, mode: Mode) {
        val m = getMetrics(mode)
        displayMetrics.density = m.density
        displayMetrics.densityDpi = m.densityDpi
        if (isSdkAtMost(SdkInt.T_33)) {
            @Suppress("DEPRECATION")
            displayMetrics.scaledDensity = m.scaledDensity
        }
    }

    @Suppress("DEPRECATION")
    fun getMetrics(mode: Mode): DisplayMetrics {
        var m = metricsCache[mode]
        if (m != null) return m

        val fitConfig = Config.fit()
        val targetDensity = when (mode) {
            Mode.WIDTH -> fitConfig.screenWidth / fitConfig.widthDensity
            Mode.HEIGHT -> fitConfig.screenHeight / fitConfig.heightDensity
            Mode.FULL_SCREEN -> {
                val scaleW = fitConfig.screenWidth / fitConfig.widthDensity
                val scaleH = fitConfig.screenHeight / fitConfig.heightDensity
                if (scaleW < scaleH) scaleW else scaleH
            }

            Mode.DEFAULT -> appMetrics.density
        }
        val targetDensityDpi = (targetDensity * 160).toInt()

        m = DisplayMetrics()
        m.density = targetDensity
        m.densityDpi = targetDensityDpi
        if (isSdkAtMost(SdkInt.T_33)) {
            val targetScaledDensity = targetDensity * (appMetrics.scaledDensity / appMetrics.density)
            m.scaledDensity = targetScaledDensity
        }

        metricsCache[mode] = m
        return m
    }
}

/**
 * 使用自己的resources, 内部维护缓存及metrics的获取
 */
@Suppress("DEPRECATION")
class FitResources private constructor(val mode: Mode, res: Resources) : Resources(
    res.assets,
    res.displayMetrics,
    res.configuration
) {

    companion object {
        private val ref = WeakHashMap<Mode, Resources>()

        fun get(mode: Mode, base: Resources): Resources {
            var res = ref[mode]
            if (res == null) {
                res = FitResources(mode, base)
                ref[mode] = res
            }
            return res
        }

        fun remove(mode: Mode) {
            ref.remove(mode)
        }

        fun clear() {
            ref.clear()
        }
    }

    private val metrics by lazy {
        val d = DisplayMetrics()
        d.setTo(super.displayMetrics)
        Fitter.fit(d, mode)
        d
    }

    override fun getDisplayMetrics(): DisplayMetrics {
        return metrics
    }
}