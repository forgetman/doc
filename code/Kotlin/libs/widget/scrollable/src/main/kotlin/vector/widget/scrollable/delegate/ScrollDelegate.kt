package vector.widget.scrollable.delegate

import vector.app.delegate.OnScrollCompatListener
import vector.widget.swiperefresh.delegate.LoadMore

interface ScrollDelegate : LoadMore.Option {
    fun addOnScrollListener(listener: OnScrollCompatListener)
}