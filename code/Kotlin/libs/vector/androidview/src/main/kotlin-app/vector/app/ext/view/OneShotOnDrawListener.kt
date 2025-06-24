package vector.app.ext.view

import android.view.View
import android.view.ViewTreeObserver
import sugar.ext.runOnMainThread

class OneShotOnDrawListener private constructor(private val view: View, private val runnable: Runnable) :
    ViewTreeObserver.OnDrawListener, View.OnAttachStateChangeListener {

    companion object {
        fun add(view: View?, runnable: Runnable?): OneShotOnDrawListener {
            if (view == null) throw NullPointerException("view == null")
            if (runnable == null) throw NullPointerException("runnable == null")
            val listener = OneShotOnDrawListener(view, runnable)
            view.viewTreeObserver.addOnDrawListener(listener)
            view.addOnAttachStateChangeListener(listener)
            return listener
        }
    }

    private var onDrawCalled: Boolean = false
    private var viewTreeObserver: ViewTreeObserver

    init {
        viewTreeObserver = view.viewTreeObserver
    }

    override fun onDraw() {
        if (onDrawCalled) return
        onDrawCalled = true
        runnable.run()

        // Cannot call removeOnDrawListener inside of onDraw
        runOnMainThread(view) {
            removeListener()
        }
    }

    private fun removeListener() {
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.removeOnDrawListener(this)
        } else {
            view.viewTreeObserver.removeOnDrawListener(this)
        }
        view.removeOnAttachStateChangeListener(this)
    }

    override fun onViewAttachedToWindow(v: View) {
        viewTreeObserver = v.viewTreeObserver
    }

    override fun onViewDetachedFromWindow(v: View) {
        removeListener()
    }
}