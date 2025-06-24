package star.model

import logger.L

/**
 * 公会
 */
class Union {
    private var hosts = mutableListOf<Host>()
    private var anchors = mutableListOf<Anchor>()

    private val rebates = Rebate()
    private val cost = Cost()
    private val tax = 0

    private lateinit var condition: Condition

    fun acceptCondition(condition: Condition) {
        cost.acceptCondition(condition)

        clear()

        for (i in 0 until condition.hostSize) {
            val h = Host()
            h.acceptCondition(condition)
            addHost(h)
        }

        for (i in 0 until condition.anchorSize) {
            val a = Anchor()
            a.acceptCondition(condition)
            addAnchor(a)
        }

        this.condition = condition
    }

    fun addHost(host: Host) {
        hosts.add(host)
    }

    fun addAnchor(anchor: Anchor) {
        anchors.add(anchor)
    }

    fun getHostEarnings(index: Int = 0) = hosts[index].earnings()
    fun getAnchorEarnings(index: Int = 0) = anchors[index].earnings()

    /**
     * 公会收入 = (总流水 * 公会分成) + 流水返点 - 公会支出 - 公会扣税
     */
    fun earnings(): Int {
        L.d("分成 = " + dividend()) // 600
        L.d("返点 = " + rebates()) // 0
        L.d("开销 = " + cost()) // 588
        L.d("税收 = " + tax())
        val dayTotal = dividend() + rebates() - cost() - tax()
        return dayTotal * condition.hallSize
    }

    fun dividend(): Int = (condition.dayFlow * condition.unionDividend / 100f).toInt()
    fun rebates() = rebates.value(condition.dayFlow)
    fun cost() = cost.value()

    fun tax() = tax

    fun clear() {
        hosts.clear()
        anchors.clear()
    }
}
