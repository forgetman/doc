package vector.widget.databinding.swiperefresh.adapter.trigger

private typealias RefreshAction = (Boolean) -> Unit

class SwipeRefreshTrigger internal constructor() {

    private var action: RefreshAction? = null

    fun trig(refresh: Boolean) {
        action?.invoke(refresh)
    }

    internal fun observe(action: RefreshAction) {
        this.action = action
    }
}