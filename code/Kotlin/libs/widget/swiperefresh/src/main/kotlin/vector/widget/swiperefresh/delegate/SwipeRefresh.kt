package vector.widget.swiperefresh.delegate

interface SwipeRefresh {
    enum class State {
        START, // 下拉
        END, // 结束下拉
    }

    enum class UiState {
        IDLE, // 闲置
        LOADING, // 加载中
        READY, // 松开后加载
        FINISH, // 加载完成
    }

    interface Listener {
        fun onSwipeStateChanged(state: State)
    }

    interface Option {
        fun startRefresh()
        fun stopRefresh()
    }
}