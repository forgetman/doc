package vector.app.delegate

/**
 * 初始化流程
 */
internal interface InitializeDelegate {

    interface Listener {
        /**
         * 数据初始化结束
         */
        fun onDataInitializeEnd() {}

        /**
         * AppBar初始化结束
         */
        fun onSystemBarInitializeEnd() {}

        /**
         * 事件初始化结束
         */
        fun onContentViewInitializeEnd() {}
    }

    fun startInitializeFlow() {
        error("startInitializeFlow() must be override")
    }

    /**
     * 初始化数据
     */
    fun initializeData() {}

    /**
     * 初始化[vector.app.appbar.AppBar] / 系统的status bar / 系统的navigation bar
     */
    fun initializeSystemBar() {}

    /**
     * 初始化视图
     */
    fun initializeContentView() {}

    fun addInitializeFlowListener(listener: Listener)

    fun useListeners(listener: (Listener) -> Unit)

    fun clearInitializeFlowListeners()
}

internal class InitializeInitializeDelegateImpl : InitializeDelegate {

    private val listeners = mutableListOf<InitializeDelegate.Listener>()

    override fun addInitializeFlowListener(listener: InitializeDelegate.Listener) {
        if (listeners.contains(listener)) return
        listeners.add(listener)
    }

    override fun useListeners(listener: (InitializeDelegate.Listener) -> Unit) {
        listeners.forEach(listener)
    }

    override fun clearInitializeFlowListeners() {
        listeners.clear()
    }
}

internal fun <T : InitializeDelegate> T.performDataInitialize() {
    initializeData()
    useListeners(InitializeDelegate.Listener::onDataInitializeEnd)
}

internal fun <T : InitializeDelegate> T.performSystemBarInitialize() {
    initializeSystemBar()
    useListeners(InitializeDelegate.Listener::onSystemBarInitializeEnd)
}

internal fun <T : InitializeDelegate> T.performContentViewInitialize() {
    initializeContentView()
    useListeners(InitializeDelegate.Listener::onContentViewInitializeEnd)
}