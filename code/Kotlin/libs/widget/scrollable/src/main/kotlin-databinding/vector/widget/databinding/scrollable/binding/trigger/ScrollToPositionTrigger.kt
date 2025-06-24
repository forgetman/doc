package vector.widget.databinding.scrollable.binding.trigger

private typealias ScrollToPositionAction = (position: Int, smoothScroll: Boolean?) -> Unit

/**
 * @author yuansui
 * @since 2019-09-02
 */
class ScrollToPositionTrigger internal constructor() : ScrollableTrigger {

    private var action: ScrollToPositionAction? = null

    fun trig(position: Int, smoothScroll: Boolean? = null) {
        action?.invoke(position, smoothScroll)
    }

    internal fun observe(action: ScrollToPositionAction) {
        this.action = action
    }
}