package tool.trigger.strategy

interface StrategyOption {
    fun onLaunch() {}
    fun onContinuation() {}
    fun onReset() {}
    fun onStart() {}
    fun onStop() {}
}

/**
 * @author yuansui
 * @since 2022/10/22
 */
abstract class Strategy : StrategyOption {

    companion object {
        @JvmField
        val NONE = object : Strategy() {}
    }

    fun interface Listener {
        fun onAchieved(stopped: Boolean)
    }

    private var listener: Listener? = null

    internal fun setListener(listener: Listener?) {
        this.listener = listener
    }

    protected fun achieved(stopped: Boolean) {
        listener?.onAchieved(stopped)
    }
}