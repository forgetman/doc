package reader.model

import sugar.ext.self
import vector.widget.swiperefresh.delegate.LoadMore

class Page {
    companion object {
        const val LIMIT = 20  // 分页加载每页的数据量
        const val START = 1
    }

    var num: Int = START

    fun refresh() = num == START

    fun change(lastState: LoadMore.State? = null) =
        if (lastState == null) {
            reset()
        } else {
            inc(lastState)
        }

    private fun reset() = self {
        num = START
    }

    private fun inc(state: LoadMore.State) = self {
        if (state == LoadMore.State.READY) {
            num++
        }
    }
}