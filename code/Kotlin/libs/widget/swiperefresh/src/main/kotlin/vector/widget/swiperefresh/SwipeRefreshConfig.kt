package vector.widget.swiperefresh

import android.content.Context
import vector.widget.swiperefresh.footer.BaseFooter
import vector.widget.swiperefresh.header.BaseSwipeHeader

object SwipeRefreshConfig {
    var swipeHeaderConstructor: ((Context) -> BaseSwipeHeader)? = null
    var footerConstructor: ((Context) -> BaseFooter)? = null
}