package star.model

import live.Live
import star.design.viewModel.DEFAULT_ANCHOR_SIZE
import star.design.viewModel.DEFAULT_ANCHOR_SUBSIDY
import star.design.viewModel.DEFAULT_DAY_DURATION
import star.design.viewModel.DEFAULT_DAY_FLOW
import star.design.viewModel.DEFAULT_EMPLOYEE_DIVIDEND
import star.design.viewModel.DEFAULT_HALL_SIZE
import star.design.viewModel.DEFAULT_HOST_SIZE
import star.design.viewModel.DEFAULT_HOST_SUBSIDY
import star.design.viewModel.DEFAULT_MONTH_DURATION
import star.design.viewModel.DEFAULT_UNION_DIVIDEND

/**
 * @author yuansui
 * @since 2020/4/17
 */
object Input {
    val employeeDividend = Live(DEFAULT_EMPLOYEE_DIVIDEND)
    val unionDividend = Live(DEFAULT_UNION_DIVIDEND)

    val dayDuration = Live(DEFAULT_DAY_DURATION)
    val monthDuration = Live(DEFAULT_MONTH_DURATION)

    val dayFlow = Live(DEFAULT_DAY_FLOW)
    val monthFlow = Live<Int>()

    val hostSize = Live(DEFAULT_HOST_SIZE)
    val hostSubsidyAnHour = Live(DEFAULT_HOST_SUBSIDY)

    val anchorSize = Live(DEFAULT_ANCHOR_SIZE)
    val anchorSubsidyAnHour = Live(DEFAULT_ANCHOR_SUBSIDY)

    val hallSize = Live(DEFAULT_HALL_SIZE)

    val rebate = InputRebate()

    class InputRebate {
        var zoneCount = Live<Int>()
        var platformCount = Live<Int>()
    }
}