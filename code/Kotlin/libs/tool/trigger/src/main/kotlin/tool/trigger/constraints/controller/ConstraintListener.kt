package tool.trigger.constraints.controller

import androidx.annotation.MainThread

internal interface ConstraintListener<T> {
    @MainThread
    fun onConstraintChanged(newValue: T)
}