package vector

/**
 * @author yuansui
 * @since 2025/6/3
 */
object Constants {
    const val ERR_NOT_FOUND = -1
    const val ERR_DEFAULT = -1
    const val INVALID_INT = Int.MIN_VALUE
    const val INVALID_FLOAT = INVALID_INT.toFloat()

    const val INTERPOLATOR_MAX = 1.0f

    /**
     * alpha
     */
    const val ALPHA_MAX = 255L
    const val ALPHA_MIN = 0L

    /**
     * [android.widget.TextView]的textSize和height之间的相差的大概比例
     * 具体计算为 textSize * 比例 = height(四舍五入取整)
     */
    const val TEXT_SIZE_TO_HEIGHT = 1.365f

    object Brand {
        // 魅族
        const val MEI_ZU = "Meizu"

        // vivo
        const val VIVO = "vivo"

        // 小米
        const val XIAO_MI = "xiaomi"

        // 华为1
        const val HUA_WEI_1 = "Huawei"

        // 华为2
        const val HUA_WEI_2 = "HUAWEI"

        // 华为3
        const val HUA_WEI_3 = "HONOR"

        // 索尼
        const val SONY = "sony"

        // 三星
        const val SAMSUNG = "samsung"

        // LG
        const val LG = "lg"

        // HTC
        const val HTC = "htc"

        // NOVA
        const val NOVA = "nova"

        // OPPO
        const val OPPO = "OPPO"

        // 乐视
        const val LE_MOBILE = "LeMobile"

        // 联想
        const val LENOVO = "lenovo"
    }
}