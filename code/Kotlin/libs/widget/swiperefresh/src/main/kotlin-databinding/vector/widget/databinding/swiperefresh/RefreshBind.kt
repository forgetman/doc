package vector.widget.databinding.swiperefresh

import vector.widget.swiperefresh.delegate.LoadMore
import vector.widget.swiperefresh.delegate.SwipeRefresh

sealed class RefreshBind {
    data class OnSwipe(val action: (option: SwipeRefresh.Option) -> Unit) :
        RefreshBind()

    data class OnLoadMore(val action: (option: LoadMore.Option, lastState: LoadMore.State) -> Unit) :
        RefreshBind()
}