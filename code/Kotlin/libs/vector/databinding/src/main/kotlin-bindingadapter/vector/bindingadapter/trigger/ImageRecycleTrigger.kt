package vector.bindingadapter.trigger

private typealias RecycleAction = () -> Unit

/**
 * @author yuansui
 * @since 2020/1/10
 */
class ImageRecycleTrigger internal constructor() {
    private var action: RecycleAction? = null

    fun trig() {
        action?.invoke()
    }

    internal fun observe(action: RecycleAction) {
        this.action = action
    }
}