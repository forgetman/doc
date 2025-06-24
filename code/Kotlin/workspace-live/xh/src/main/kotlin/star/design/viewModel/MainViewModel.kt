package star.design.viewModel

import android.app.Application
import live.Live
import live.ext.requireValue
import live.ext.setValue
import star.design.repo.MainRepo
import star.model.Condition
import vector.app.viewmodel.ViewModelEx
import java.io.Serializable

const val DEFAULT_EMPLOYEE_DIVIDEND = 45
const val DEFAULT_UNION_DIVIDEND = 5

const val DEFAULT_DAY_DURATION = 12
const val DEFAULT_MONTH_DURATION = 24
const val DEFAULT_DAY_FLOW = 2000

const val DEFAULT_HOST_SIZE = 1
const val DEFAULT_HOST_SUBSIDY = 0

const val DEFAULT_ANCHOR_SIZE = 8
const val DEFAULT_ANCHOR_SUBSIDY = 0

const val DEFAULT_HALL_SIZE = 1


/**
 * @author yuansui
 * @since 2020-04-10
 */
class MainViewModel(app: Application) : ViewModelEx(app) {

    private val repo = MainRepo()

    val input = Input()

    val unionMonthEarnings = Live<String>()
    val unionDayEarnings = Live<String>()

    val singleHostDayEarnings = Live<String>()
    val singleHostMonthEarnings = Live<String>()

    val singleAnchorDayEarnings = Live<String>()
    val singleAnchorMonthEarnings = Live<String>()
    val allAnchorDayEarnings = Live<String>()
    val allAnchorMonthEarnings = Live<String>()

    val newSettleInRebate = Live<String>()
    val rebate = Live<String>()

    val newPayerData = NewPayerData()

    fun onDayFlowChanged(newDayFlow: Int) {
        val new = newDayFlow * input.monthDuration.requireValue()
        val old = input.monthFlow.value
        if (old != new) {
            input.monthFlow.value = new
        }
    }

    fun onMonthFlowChanged(newMonthFlow: Int) {
        val new = newMonthFlow / input.monthDuration.requireValue()
        val old = input.dayFlow.value
        if (old != new) {
            input.dayFlow.value = new
        }
    }

    fun onUnionDividendChanged(newDividend: Int) {
        val old = input.unionDividend.value
        if (old != newDividend) {
            input.unionDividend.value = newDividend
            input.employeeDividend.value = 50 - newDividend
        }
    }

    fun onAnchorDividendChanged(newDividend: Int) {
        val old = input.employeeDividend.value
        if (old != newDividend) {
            input.employeeDividend.value = newDividend
            input.unionDividend.value = 50 - newDividend
        }
    }

    fun calc() {
        val c = Condition().apply {
            unionDividend = input.unionDividend.value ?: DEFAULT_UNION_DIVIDEND
            employeeDividend = input.employeeDividend.value ?: DEFAULT_EMPLOYEE_DIVIDEND

            dayDuration = input.dayDuration.value ?: DEFAULT_DAY_DURATION
            monthDuration = input.monthDuration.value ?: DEFAULT_MONTH_DURATION

            dayFlow = input.dayFlow.value ?: DEFAULT_DAY_FLOW

            hostSize = input.hostSize.value ?: DEFAULT_HOST_SIZE
            hostSubsidy = input.hostSubsidyAnHour.value ?: DEFAULT_HOST_SUBSIDY

            anchorSize = input.anchorSize.value ?: DEFAULT_ANCHOR_SIZE
            anchorSubsidy = input.anchorSubsidyAnHour.value ?: DEFAULT_ANCHOR_SUBSIDY

            hallSize = input.hallSize.value ?: DEFAULT_HALL_SIZE
        }

        val r = repo.calc(c)
        unionDayEarnings.value = r.unionDayEarnings
        unionMonthEarnings.value = r.unionMonthEarnings

        singleAnchorDayEarnings.setValue(r.singleAnchorDayEarnings)
        singleAnchorMonthEarnings.setValue(r.singleAnchorDayEarnings * c.monthDuration)
        allAnchorDayEarnings.setValue(r.singleAnchorDayEarnings * c.anchorSize)
        allAnchorMonthEarnings.setValue(r.singleAnchorDayEarnings * c.anchorSize * c.monthDuration)

        singleHostDayEarnings.setValue(r.singleHostDayEarnings)
        singleHostMonthEarnings.setValue(r.singleHostDayEarnings * c.monthDuration)
    }
}

class Input {
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
}

class NewPayerData : Serializable {
    var zoneCount: Int = 50
    var platformCount: Int = 10
}
