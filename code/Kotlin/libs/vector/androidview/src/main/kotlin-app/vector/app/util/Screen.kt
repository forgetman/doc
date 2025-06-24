@file:Suppress("unused")

package vector.app.util

import android.graphics.Point
import android.view.WindowManager
import androidx.core.hardware.display.DisplayManagerCompat
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.systemService
import vector.appContext

enum class DensityLevel(val density: Float, val dpi: Int) {
    UN_KNOW(0.0f, 0), L(0.75f, 120), M(1.0f, 160), H(1.5f, 240), XH(2.0f, 320), XXH(3.0f, 480), XXXH(4.0f, 640);

    companion object {
        fun match(dpi: Int): DensityLevel {
            return when {
                dpi <= L.dpi -> L
                dpi <= M.dpi -> M
                dpi <= H.dpi -> H
                dpi <= XH.dpi -> XH
                dpi <= XXH.dpi -> XXH
                dpi <= XXXH.dpi -> XXXH
                else -> UN_KNOW
            }
        }
    }
}

@Suppress("MemberVisibilityCanBePrivate")
object Screen {

    var width = 0 // 屏幕宽度
        private set

    /**
     * 屏幕高度: 包含statusBar不包含navigationBar的高度
     */
    var height = 0
        private set

    var physicalWidth = 0 // 屏幕物理宽度
        private set

    var physicalHeight = 0 // 屏幕物理高度
        private set

    /**
     * @see [DensityLevel]
     */
    var density = 0f
        private set

    var densityLevel: DensityLevel = DensityLevel.UN_KNOW
        private set

    init {
        reset()
    }

    fun reset() {
        val metrics = appContext.resources.displayMetrics
        width = metrics.widthPixels
        height = metrics.heightPixels
        density = metrics.density
        densityLevel = DensityLevel.match(metrics.densityDpi)

        @Suppress("DEPRECATION") val display = if (isSdkAtLeast(SdkInt.R_30)) {
            val ds = DisplayManagerCompat.getInstance(appContext).displays
            if (ds.isNotEmpty()) ds[0] else {
                val service = appContext.systemService<WindowManager>()
                service.defaultDisplay
            }
        } else {
            val service = appContext.systemService<WindowManager>()
            service.defaultDisplay
        }

        val outPoint = Point()

        // 可能有虚拟按键的情况
        val mgr = appContext.systemService<WindowManager>()
        if (isSdkAtLeast(SdkInt.R_30)) {
            val bounds = mgr.currentWindowMetrics.bounds
            outPoint.x = bounds.right
            outPoint.y = bounds.bottom
        } else {
            @Suppress("DEPRECATION") display?.getRealSize(outPoint)
        }

        physicalWidth = outPoint.x
        physicalHeight = outPoint.y
    }

    /**
     * 是否有虚拟导航栏
     * 如果普通区域高度和物理高度不相等, 认为其有虚拟导航栏
     * PS: 无法判断全面屏手机是否有开启全面屏手势模式(开启后界面虽然无虚拟导航栏, 但其他数值正常)
     */
    @JvmStatic
    val hasVirtualBar: Boolean by lazy {
        (height - statusBarHeight) != physicalHeight
    }

    /**
     * 获取状态栏高度(未经缩放处理的原始px)
     */
    @JvmStatic
    val statusBarHeight: Int by lazy {
        val id = Res.Android.getIdentifier("status_bar_height", Res.Type.DIMEN)
        Res.Android.getDimensionPixelSize(id)
    }

    /**
     * 获取底部导航栏高度(未经缩放处理的原始px)
     */
    @JvmStatic
    val navigationBarHeight: Int by lazy {
        val id = Res.Android.getIdentifier("navigation_bar_height", Res.Type.DIMEN)
        Res.Android.getDimensionPixelSize(id)
    }
}