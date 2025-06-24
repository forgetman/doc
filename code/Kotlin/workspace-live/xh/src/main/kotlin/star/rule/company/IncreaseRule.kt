package star.rule.company

/**
 * @author yuansui
 * @since 2020/4/21
 */
abstract class IncreaseRule(flow: Int) : CRule(flow)

class DividendRule(flow: Int) : IncreaseRule(flow) {

    override fun value(): Int {
        return 10
    }

}

//class RebateRule(flow: Int) : IncreaseRule(flow) {
//
//    override fun value(): Int {
//        return 0
//    }
//}