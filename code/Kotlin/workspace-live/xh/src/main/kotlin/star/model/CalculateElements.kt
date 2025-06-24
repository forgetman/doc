package star.model

import vector.EMPTY

/**
 * @author yuansui
 * @since 2020/4/12
 */
class Condition {
    var unionDividend: Int = 0
    var employeeDividend: Int = 0

    var dayDuration: Int = 0
    var monthDuration: Int = 0
    var dayFlow: Int = 0

    var hostSize: Int = 0
    var hostSubsidy: Int = 0

    var anchorSize: Int = 0
    var anchorSubsidy: Int = 0

    var hallSize: Int = 0
}

class Result {
    var unionDayEarnings: String = EMPTY
    var unionMonthEarnings: String = EMPTY

    var singleAnchorDayEarnings: Int = 0
    var singleHostDayEarnings: Int = 0
}