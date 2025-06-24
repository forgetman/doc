package tool.trigger.strategy

import sugar.collection.safeMutableListOf
import sugar.ext.collectElements

/**
 * @author yuansui
 * @since 2023/4/19
 */
class MediatorStrategy : Strategy() {

    private val sources = safeMutableListOf<Source<*>>()
    private var started: Boolean = false

    override fun onLaunch() {
        sources.forEachElement(StrategyOption::onLaunch)
    }

    override fun onContinuation() {
        sources.forEachElement(StrategyOption::onContinuation)
    }

    override fun onReset() {
        sources.forEachElement(StrategyOption::onReset)
    }

    override fun onStart() {
        started = true
        sources.forEachElement(StrategyOption::onStart)
    }

    override fun onStop() {
        started = false
        sources.forEachElement(StrategyOption::onStop)
    }

    fun <S : Strategy> addSource(source: S, listener: Listener? = null) {
        val e = Source(source) { stopped ->
            listener?.onAchieved(stopped)
            achieved(stopped)
        }
        if (sources.contains(e)) return
        sources.add(e)
        e.plug()

        if (started) {
            e.onStart()
        }
    }

    fun <S : Strategy> removeSource(toRemote: S) {
        sources.collectElements()?.firstOrNull { source ->
            source.strategy == toRemote
        }?.unplug()
    }

    private class Source<T : Strategy>(val strategy: T, val listener: Listener) : Listener, StrategyOption {

        fun plug() {
            strategy.setListener(this)
        }

        fun unplug() {
            strategy.setListener(null)
            strategy.onStop()
        }

        override fun onAchieved(stopped: Boolean) {
            listener.onAchieved(stopped)
        }

        override fun onLaunch() {
            strategy.onLaunch()
        }

        override fun onContinuation() {
            strategy.onContinuation()
        }

        override fun onReset() {
            strategy.onReset()
        }

        override fun onStart() {
            strategy.onStart()
        }

        override fun onStop() {
            strategy.onStop()
        }
    }
}