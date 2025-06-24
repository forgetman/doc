package vector.app.fitter

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FitStrategy(val value: Mode)

enum class Mode {
    DEFAULT, // 原始
    WIDTH, // 按宽度
    HEIGHT, // 按高度
    FULL_SCREEN // 按宽高综合计算
}