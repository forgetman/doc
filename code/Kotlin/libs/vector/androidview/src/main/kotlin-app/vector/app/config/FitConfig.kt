package vector.app.config

import vector.app.fitter.Mode
import vector.app.util.Screen

/**
 * 设置缩放适配的基础属性
 *
 * @author yuansui
 */
class FitConfig private constructor() {

    companion object {
        fun build(init: FitConfig.() -> Unit): FitConfig = FitConfig().apply(init)
    }

    var density = 3f
    var width = 1080f // 设计图宽, 单位: px
    var height = 1920f // 设计图高, 单位: px
    var mode: Mode = Mode.WIDTH

    var screenWidth = Screen.width // 屏幕宽, 单位: px
    var screenHeight = Screen.height // 屏幕高, 单位: px

    inline val widthDensity: Float get() = width / density
    inline val heightDensity: Float get() = height / density
}
