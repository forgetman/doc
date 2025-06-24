package lib.base.model

import vector.swiperefresh.widget.LoadMore

class Page {
    companion object {
        const val LIMIT = 20  // 分页加载每页的数据量
        const val START = 0
    }

    var num: Int = START

    fun refresh() = num == START

    fun change(state: LoadMore.State? = null) =
        if (state == null) {
            reset()
        } else {
            inc(state)
        }

    private fun reset(): Page {
        num = START
        return this
    }

    private fun inc(state: LoadMore.State): Page {
        if (state == LoadMore.State.LOADING) {
            num++
        }
        return this
    }
}