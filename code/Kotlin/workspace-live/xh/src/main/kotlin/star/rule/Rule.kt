package star.rule

import star.rule.company.CRule
import star.rule.company.DividendRule
import star.rule.employee.ERule


/**
 * @author yuansui
 * @since 2020/4/19
 */
interface Rule {
    fun value(): Int
}

object Rules {
    private val cRules = mutableListOf<CRule>()
    private val eRules = mutableListOf<ERule>()

    fun append(rule: Rule) {
        when (rule) {
            is CRule -> cRules.add(rule)
            is ERule -> eRules.add(rule)
        }
    }

    fun getRules() {

    }

    fun clear() {
        cRules.clear()
        eRules.clear()
    }
}

class TestRule {

    init {
        Rules.append(DividendRule(1000))
    }
}