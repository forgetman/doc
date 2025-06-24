package vector.bindingadapter.trigger

/**
 * @author yuansui
 * @since 2019-10-19
 */

private typealias ClearAction = () -> Unit

class EditClearTrigger internal constructor() {

    private var action: ClearAction? = null

    fun trig() {
        action?.invoke()
    }

    internal fun observe(action: ClearAction) {
        this.action = action
    }
}