@file:Suppress("unused")

package vector.app.ext

import android.view.View
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.BOTTOM
import androidx.constraintlayout.widget.ConstraintSet.END
import androidx.constraintlayout.widget.ConstraintSet.MATCH_CONSTRAINT
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.constraintlayout.widget.ConstraintSet.START
import androidx.constraintlayout.widget.ConstraintSet.TOP
import androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT
import sugar.ext.self
import vector.app.ext.view.ensureIdExist

class ConstraintStyle internal constructor(private val set: ConstraintSet) {

    inner class Theme internal constructor(private val viewId: Int) {
        internal constructor(view: View) : this(view.id) {
            if (view.id == View.NO_ID) throw IllegalStateException("需要先设置view的id")
        }

        fun match(
            startToStart: Int = PARENT_ID,
            endToEnd: Int = PARENT_ID,
            topToTop: Int = PARENT_ID,
            bottomToBottom: Int = PARENT_ID
        ) {
            set.apply {
                constrainWidth(viewId, MATCH_CONSTRAINT)
                constrainHeight(viewId, MATCH_CONSTRAINT)
                connect(viewId, START, startToStart, START)
                connect(viewId, END, endToEnd, END)
                connect(viewId, TOP, topToTop, TOP)
                connect(viewId, BOTTOM, bottomToBottom, BOTTOM)
            }
        }

        fun matchWidth(startToStart: Int = PARENT_ID, endToEnd: Int = PARENT_ID) {
            set.constrainWidth(viewId, MATCH_CONSTRAINT)
            set.connect(viewId, START, startToStart, START)
            set.connect(viewId, END, endToEnd, END)
        }

        fun matchHeight(topToTop: Int = PARENT_ID, bottomToBottom: Int = PARENT_ID) {
            set.constrainHeight(viewId, MATCH_CONSTRAINT)
            set.connect(viewId, TOP, topToTop, TOP)
            set.connect(viewId, BOTTOM, bottomToBottom, BOTTOM)
        }

        fun alignCenter(
            startToStart: Int = PARENT_ID,
            endToEnd: Int = PARENT_ID,
            topToTop: Int = PARENT_ID,
            bottomToBottom: Int = PARENT_ID
        ) {
            set.apply {
                connect(viewId, START, startToStart, START)
                connect(viewId, END, endToEnd, END)
                connect(viewId, TOP, topToTop, TOP)
                connect(viewId, BOTTOM, bottomToBottom, BOTTOM)
            }
        }

        fun alignBottomCenter(
            startToStart: Int = PARENT_ID,
            endToEnd: Int = PARENT_ID,
            bottomToBottom: Int = PARENT_ID
        ) {
            set.apply {
                connect(viewId, START, startToStart, START)
                connect(viewId, END, endToEnd, END)
                connect(viewId, BOTTOM, bottomToBottom, BOTTOM)
            }
        }
    }

    fun withTheme(viewId: Int, action: Theme.() -> Unit) {
        action(Theme(viewId))
    }

    fun withTheme(view: View?, action: Theme.() -> Unit) {
        action(Theme(view ?: return))
    }

    interface AlignHorizontal
    class Start : AlignHorizontal
    class End : AlignHorizontal

    interface AlignVertical
    class Top : AlignVertical
    class Bottom : AlignVertical

    inner class Rule internal constructor(private val viewId: Int) {
        internal constructor(view: View) : this(view.id) {
            if (view.id == View.NO_ID) throw IllegalStateException("需要先设置view的id")
        }

        val start by lazy { Start() }
        val end by lazy { End() }
        val top by lazy { Top() }
        val bottom by lazy { Bottom() }

        infix fun AlignHorizontal.toStart(@IdRes id: Int) {
            if (this is Start) {
                set.connect(viewId, START, id, START)
            } else {
                set.connect(viewId, END, id, START)
            }
        }

        infix fun AlignHorizontal.toEnd(@IdRes id: Int) {
            if (this is Start) {
                set.connect(viewId, START, id, END)
            } else {
                set.connect(viewId, END, id, END)
            }
        }

        infix fun AlignVertical.toTop(@IdRes id: Int) {
            if (this is Top) {
                set.connect(viewId, TOP, id, TOP)
            } else {
                set.connect(viewId, BOTTOM, id, TOP)
            }
        }

        infix fun AlignVertical.toBottom(@IdRes id: Int) {
            if (this is Top) {
                set.connect(viewId, TOP, id, BOTTOM)
            } else {
                set.connect(viewId, BOTTOM, id, BOTTOM)
            }
        }
    }

    fun withRule(viewId: Int, action: Rule.() -> Unit) {
        action(Rule(viewId))
    }

    fun withRule(view: View?, action: Rule.() -> Unit) {
        action(Rule(view ?: return))
    }

    fun constrainSizeMatch(view: View?) {
        withViewId(view) { constrainSizeMatch(it) }
    }

    fun constrainSizeMatch(viewId: Int) {
        set.constrainWidth(viewId, MATCH_CONSTRAINT)
        set.constrainHeight(viewId, MATCH_CONSTRAINT)
    }

    fun constrainSizeWrap(view: View?) {
        withViewId(view) { constrainSizeWrap(it) }
    }

    fun constrainSizeWrap(viewId: Int) {
        set.constrainWidth(viewId, WRAP_CONTENT)
        set.constrainHeight(viewId, WRAP_CONTENT)
    }

    fun constrainWidth(view: View?, width: Int) {
        withViewId(view) { constrainWidth(it, width) }
    }

    fun constrainWidth(viewId: Int, width: Int) {
        set.constrainWidth(viewId, width)
    }

    fun constrainHeight(view: View?, height: Int) {
        withViewId(view) { constrainHeight(it, height) }
    }

    fun constrainHeight(viewId: Int, height: Int) {
        set.constrainHeight(viewId, height)
    }

    private inline fun withViewId(view: View?, action: (viewId: Int) -> Unit) {
        view ?: return
        view.ensureIdExist()
        action(view.id)
    }
}

fun ConstraintSet.asStyle(action: ConstraintStyle.() -> Unit) = self {
    val style = ConstraintStyle(this)
    action(style)
}



