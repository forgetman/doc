package vector.widget.databinding.scrollable.binding.trigger

/**
 * @author yuansui
 * @since 2019-08-27
 */
object ScrollableBindTrigger {
    fun scrollToTop() = ScrollToTopTrigger()
    fun scrollBy() = ScrollByTrigger()
    fun scrollToPosition() = ScrollToPositionTrigger()
}