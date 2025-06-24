package star.design.repo

import star.model.Condition
import star.model.Result
import star.model.Union

/**
 * @author yuansui
 * @since 2020/4/12
 */
class MainRepo {

    private val union = Union()

    fun calc(condition: Condition): Result {
        union.acceptCondition(condition)

        val result = Result()
        val day = union.earnings()
        result.unionDayEarnings = day.toString()
        result.unionMonthEarnings = (day * condition.monthDuration).toString()

        result.singleHostDayEarnings = union.getHostEarnings()
        result.singleAnchorDayEarnings = union.getAnchorEarnings()

        return result
    }
}