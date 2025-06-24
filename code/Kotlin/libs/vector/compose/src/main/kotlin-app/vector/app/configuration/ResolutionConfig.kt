package vector.app.configuration

/**
 * 设置缩放适配的基础属性
 *
 * @author yuansui
 */
class ResolutionConfig private constructor() {

    companion object {
        fun build(init: ResolutionConfig.() -> Unit) = ResolutionConfig().apply(init)
    }

    var density = 3f
    var width = 1080f // 设计图宽, 单位: px
    var height = 1920f // 设计图高, 单位: px

    inline val widthDensity: Float get() = width / density
    inline val heightDensity: Float get() = height / density
}
