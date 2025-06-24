package star.model

/**
 * @author yuansui
 * @since 2020/4/11
 */
class Cost {
    /**
     * 概念
     * 每小时开销 = (主持数量 * 主持每小时补贴) + (主播数量 * 主播每小时补贴)
     * 总开销 = 每小时开销 * 开播时长
     *
     * 公式
     * 总开销 = 主播总开销 + 主持总开销
     */

    private lateinit var c: Condition

    fun acceptCondition(c: Condition) {
        this.c = c
    }

    fun value(): Int {
        val hostCosts = c.hostSize * c.hostSubsidy
        val anchorCosts = c.anchorSize * c.anchorSubsidy
        return (hostCosts + anchorCosts) * c.dayDuration
    }
}