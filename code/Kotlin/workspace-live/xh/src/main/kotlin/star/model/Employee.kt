package star.model

/**
 * @author yuansui
 * @since 2020/4/11
 */
abstract class Employee {
    var level: Int = -1 // 档位

    protected var condition: Condition? = null

    /**
     * 公式
     * 收入 = 流水分成 + (每小时补贴 * 开播时长)
     */
    fun earnings(): Int {
        val c = condition ?: return 0
        return (c.dayFlow * (c.employeeDividend / 100f) / (c.hostSize + c.anchorSize) + subsidy()).toInt()
    }

    fun acceptCondition(condition: Condition) {
        this.condition = condition
    }

    abstract fun subsidy(): Int
}

class Anchor : Employee() {
    override fun subsidy(): Int {
        val c = condition ?: return 0
        return c.anchorSubsidy * c.dayDuration
    }
}

class Host : Employee() {
    override fun subsidy(): Int {
        val c = condition ?: return 0
        return c.hostSubsidy * c.dayDuration
    }
}