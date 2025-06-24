package tool.trigger.constraints.controller

import tool.trigger.TriggerSpec
import tool.trigger.constraints.tracker.ConstraintTracker

/**
 * @author yuansui
 * @since 2023/4/17
 */
internal abstract class ConstraintController<T>(private val tracker: ConstraintTracker<T>) : ConstraintListener<T> {
    interface OnConstraintUpdatedCallback {
        fun onConstraintMet(specs: List<TriggerSpec>)

        fun onConstraintNotMet(specs: List<TriggerSpec>)
    }

    private val matchingSpecs = mutableListOf<TriggerSpec>()
    private var currentValue: T? = null

    /**
     * Sets the callback to inform when constraints change.  This callback is also triggered the
     * first time it is set.
     */
    var callback: OnConstraintUpdatedCallback? = null
        set(value) {
            if (field !== value) {
                field = value
                updateCallback(value, currentValue)
            }
        }

    fun replace(workSpecs: Iterable<TriggerSpec>) {
        matchingSpecs.clear()
        workSpecs.filterTo(matchingSpecs) { hasConstraint(it) }

        if (matchingSpecs.isEmpty()) {
            tracker.removeListener(this)
        } else {
            tracker.addListener(this)
        }
        updateCallback(callback, currentValue)
    }

    fun reset() {
        if (matchingSpecs.isNotEmpty()) {
            matchingSpecs.clear()
            tracker.removeListener(this)
        }
    }

    fun isSpecConstrained(spec: TriggerSpec): Boolean {
        val value = currentValue
        return (value != null && isConstrained(value) && matchingSpecs.contains(spec))
    }


    abstract fun hasConstraint(spec: TriggerSpec): Boolean
    abstract fun isConstrained(value: T): Boolean

    private fun updateCallback(callback: OnConstraintUpdatedCallback?, currentValue: T?) {
        if (matchingSpecs.isEmpty() || callback == null) {
            return
        }
        if (currentValue == null || isConstrained(currentValue)) {
            callback.onConstraintNotMet(matchingSpecs)
        } else {
            callback.onConstraintMet(matchingSpecs)
        }
    }

    override fun onConstraintChanged(newValue: T) {
        currentValue = newValue
        updateCallback(callback, currentValue)
    }
}