package vector.compat.notch

enum class DisplayCutoutMode {
    DEFAULT, // 默认情况下，全屏窗口不会使用到刘海区域，非全屏窗口可正常使用刘海区域
    NEVER, // 窗口不允许和刘海屏重叠
    SHORT_EDGES, // 窗口允许在全屏下使用刘海区域
    ALWAYS, // 无条件允许使用
}