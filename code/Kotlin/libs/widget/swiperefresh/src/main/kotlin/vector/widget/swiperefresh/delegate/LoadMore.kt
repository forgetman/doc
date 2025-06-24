package vector.widget.swiperefresh.delegate

interface LoadMore {

    enum class State {
        READY,
        LOADING,
        ERROR,
        DETACH
    }

    interface Listener {
        fun onLoading(lastState: State)
    }

    interface Option {
        fun ready()
        fun stop(hasError: Boolean)

        fun setListener(listener: Listener)
    }
}