package vector.widget.databinding.scrollable.binding.trigger

private typealias ScrollByAction = (x: Int, y: Int, smoothScroll: Boolean?) -> Unit

/**
 * @author yuansui
 * @since 2019-09-02
 */
class ScrollByTrigger internal constructor() : ScrollableTrigger {

    private var action: ScrollByAction? = null

    fun trig(x: Int = 0, y: Int = 0, smoothScroll: Boolean? = null) {
        action?.invoke(x, y, smoothScroll)
    }

    internal fun observe(action: ScrollByAction) {
        this.action = action
    }
}