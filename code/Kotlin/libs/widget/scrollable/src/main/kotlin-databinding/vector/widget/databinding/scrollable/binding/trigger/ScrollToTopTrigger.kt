package vector.widget.databinding.scrollable.binding.trigger

internal fun interface ToTopCallback {
    fun trig(smoothScroll: Boolean?)
}

class ScrollToTopTrigger internal constructor() : ScrollableTrigger {

    private var action: ToTopCallback? = null

    fun trig(smoothScroll: Boolean? = null) {
        action?.trig(smoothScroll)
    }

    internal fun observe(action: ToTopCallback) {
        this.action = action
    }
}