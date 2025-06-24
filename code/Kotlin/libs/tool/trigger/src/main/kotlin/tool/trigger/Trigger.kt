package tool.trigger

import android.content.Context
import logger.L
import sugar.collection.safeMutableListOf
import tool.trigger.constraints.Constraints
import tool.trigger.constraints.NetworkType
import tool.trigger.constraints.TriggerConstraintsCallback
import tool.trigger.constraints.TriggerConstraintsTracker
import tool.trigger.constraints.TriggerConstraintsTrackerImpl
import tool.trigger.constraints.tracker.BatteryChargingTracker
import tool.trigger.constraints.tracker.BatteryTemperatureNotHighTracker
import tool.trigger.constraints.tracker.NetworkStateTracker
import tool.trigger.constraints.tracker.PowerDownRateNotHighTracker
import tool.trigger.constraints.tracker.ScreenOffTracker
import tool.trigger.constraints.tracker.ScreenOnTracker
import tool.trigger.constraints.tracker.Trackers
import tool.trigger.strategy.Strategy


/**
 * @author yuansui
 * @since 2023/4/17
 */
interface Trigger {
    /**
     * 开始
     */
    fun launch()

    /**
     * 继续
     */
    fun continuation()

    /**
     * 重置
     */
    fun reset()

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)

    fun interface Listener {
        fun onTrigger()
    }
}

interface TriggerBuilder {
    fun setTag(tag: String)
    fun setConstraints(constraints: Constraints)
    fun applyStrategy(strategy: Strategy)
    fun build(): Trigger
}

fun Trigger(context: Context, builderAction: TriggerBuilder.() -> Unit): Trigger {
    val builder = TriggerBuilderImpl(context)
    builderAction(builder)
    return builder.build()
}

private class TriggerBuilderImpl(private val context: Context) : TriggerBuilder {
    private var strategy: Strategy = Strategy.NONE
    private var constraints: Constraints = Constraints.NONE
    private var tag: String? = null

    override fun setTag(tag: String) {
        this.tag = tag
    }

    override fun setConstraints(constraints: Constraints) {
        this.constraints = constraints
    }

    override fun applyStrategy(strategy: Strategy) {
        this.strategy = strategy
    }

    override fun build(): Trigger {
        constraints.tag = tag
        return TriggerImpl(context, constraints, strategy, tag)
    }
}

internal class TriggerSpec(val constraints: Constraints) {
    override fun toString(): String {
        return "TriggerSpec, $constraints"
    }
}

private class TriggerImpl(
    context: Context,
    private val constraints: Constraints,
    private val strategy: Strategy,
    private val tag: String?
) : Trigger {

    companion object {
        private const val LOG_TAG = "Trigger"
    }

    enum class State {
        LAUNCHED,
        STARTED,
        STOPPED,
        RESET
    }

    private val lock = Any()
    private var state: State = State.RESET
        set(newState) {
            synchronized(lock) {
                if (field == newState) return
                field = newState
                printLog("state = $field")
            }
        }

    private val listeners = safeMutableListOf<Trigger.Listener>()
    private var allMet: Boolean = false

    private val spec = TriggerSpec(constraints)

    private val tracker: TriggerConstraintsTracker by lazy {
        TriggerConstraintsTrackerImpl(
            Trackers(
                if (constraints.requiredNetworkType != NetworkType.NOT_REQUIRED) NetworkStateTracker(context) else null,
                if (constraints.requiresCharging) BatteryChargingTracker(context) else null,
                if (constraints.requiresTemperatureNotHigh) BatteryTemperatureNotHighTracker(context) else null,
                if (constraints.requiresScreenOn) ScreenOnTracker(context) else null,
                if (constraints.requiresScreenOff) ScreenOffTracker(context) else null,
                if (constraints.requiresPowerDownRateNotHigh) PowerDownRateNotHighTracker(context) else null,
            ),
            object : TriggerConstraintsCallback {
                override fun onAllConstraintsMet(specs: List<TriggerSpec>) {
                    printLog("onAllConstraintsMet = $specs")
                    if (!specs.contains(spec)) return
                    allMet = true
                    if (listeners.isNotEmpty()) start()
                }

                override fun onAllConstraintsNotMet(specs: List<TriggerSpec>) {
                    printLog("onAllConstraintsNotMet = $specs")
                    if (!specs.contains(spec)) return
                    allMet = false
                    stop()
                }
            })
    }


    init {
        strategy.setListener { stopped ->
            if (stopped) {
                // 如果Strategy已经自行停止, 跟随改变状态为停止
                state = State.STOPPED
            }
            listeners.forEachElement(Trigger.Listener::onTrigger)
        }

        if (constraints == Constraints.NONE) allMet = true
    }

    override fun launch() {
        when (state) {
            State.RESET -> {
                if (state != State.RESET) return
                printLog("launch")

                state = State.LAUNCHED

                if (constraints == Constraints.NONE) {
                    start()
                } else {
                    tracker.replace(listOf(spec))
                }
            }

            State.STOPPED -> continuation()
            else -> {
                // do nothing
            }
        }
    }

    override fun continuation() {
        if (state != State.STOPPED) return // 只有暂停了才能继续
        // FIXME 暂时不需要区分手动和自动暂停
        printLog("continuation")
        state = State.STARTED
        strategy.onContinuation()
    }

    override fun reset() {
        if (state == State.RESET) return

        if (constraints == Constraints.NONE) {
            stop()
        }

        state = State.RESET

        if (constraints != Constraints.NONE) {
            tracker.reset()
        }
        strategy.onReset()
    }

    private fun start() {
        if (state == State.STARTED || state == State.RESET) return
        state = State.STARTED
        strategy.onStart()
    }

    private fun stop() {
        if (state != State.STARTED) return
        state = State.STOPPED
        strategy.onStop()
    }

    override fun addListener(listener: Trigger.Listener) {
        listeners.add(listener)
        if (allMet) {
            start()
        }
    }

    override fun removeListener(listener: Trigger.Listener) {
        listeners.remove(listener)
        if (listeners.isEmpty()) {
            stop()
        }
    }

    private fun printLog(msg: String) {
        val message = if (!tag.isNullOrEmpty()) "tag = $tag, $msg" else msg
        L.d(LOG_TAG, message)
    }
}