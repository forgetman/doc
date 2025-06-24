package vector.app.config

import android.view.View
import vector.app.os.Dimension

class ListConfig private constructor() {

    companion object {
        const val LIST_LIMIT = 20
        const val LIST_OFFSET = 0

        fun build(init: ListConfig.() -> Unit): ListConfig = ListConfig().apply(init)
    }

    var initOffset = LIST_OFFSET
    var limit = LIST_LIMIT
    var dragDistance: Dimension? = null // 不能根据header高度自适应
    var overScrollMode = View.OVER_SCROLL_ALWAYS
}
