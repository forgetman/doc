package vector.app.frag

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import sugar.ext.getAnnotation
import sugar.ext.throwIfNull
import vector.app.UIHost
import vector.app.appbar.AppBar
import vector.app.decor.CreateDecorErrorView
import vector.app.decor.CreateDecorLoadingView
import vector.app.decor.DecorPlaceHolder
import vector.app.decor.DecorView
import vector.app.decor.ViewState
import vector.app.delegate.InitializeDelegate
import vector.app.delegate.InitializeInitializeDelegateImpl
import vector.app.delegate.performContentViewInitialize
import vector.app.delegate.performDataInitialize
import vector.app.delegate.performSystemBarInitialize
import vector.app.config.Config
import vector.app.ext.cloneLayoutInflater
import vector.app.ext.createResourceContext
import vector.app.ext.view.doOnPreDraw
import vector.app.fitter.FitStrategy
import vector.util.InjectUtil

enum class LazyLoadMode {
    NONE, // 不使用延迟加载
    IDLE, // 利用UI空闲时机加载
    RESUME // 切换显示之后加载
}

/**
 * 不带mvvm
 * @author yuansui
 * @since 2018/4/20
 */
abstract class SimpleFragEx :
    Fragment(),
    UIHost,
    InitializeDelegate by InitializeInitializeDelegateImpl() {

    override val uiView: View?
        get() = decorView

    private var decorView: DecorView? = null

    val appBar: AppBar by lazy {
        decorView?.appBar.throwIfNull("decorView can not be null")
    }

    var viewState: ViewState? = null
        set(value) {
            if (value == null) return
            decorView?.viewState = value
            field = value
        }

    /**
     * 适用于
     * [vector.widget.viewpager.ViewPager]
     * [vector.widget.viewpager2.ViewPager2]
     * 单独使用是无效的, 加载机制问题, 但是配合DecorView的placeHolder延迟加载机制, 就能产生作用
     */
    open val lazyLoadMode: LazyLoadMode = LazyLoadMode.NONE

    private val idleHandler = lazy {
        MessageQueue.IdleHandler {
            val state = lifecycle.currentState
            if (state.isAtLeast(Lifecycle.State.STARTED) && state != Lifecycle.State.DESTROYED) {
                decorView?.inflate()
                startInitializeFlow()
            }
            false
        }
    }

    protected var initializeFlowOver = false // 初始化流程是否结束

    private var fitContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            // 暴力舍弃掉, 修复重叠的问题, 但是状态会无法保存
            val transaction = parentFragmentManager.beginTransaction()
            transaction.remove(this)
            transaction.commitAllowingStateLoss()
        }

        InjectUtil.bind(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (decorView != null) {
            return decorView
        }

        initDecorView()
        performDataInitialize()

        return decorView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (initializeFlowOver) return

        // 导航栏直接初始化, 不影响切换过程的显示
        performSystemBarInitialize()

        when (lazyLoadMode) {
            LazyLoadMode.NONE -> startInitializeFlow()
            LazyLoadMode.IDLE -> addIdleHandler()
            LazyLoadMode.RESUME -> {
                // do nothing, see #onResumed()
            }
        }
    }

    private fun initDecorView() {
        decorView = onCreateDecorView()
        decorView?.errorClickListener = View.OnClickListener {
            onRetryClick()
        }
    }

    @CallSuper
    open fun onCreateDecorView(): DecorView {
        return DecorView.create(requireContext()) {
            val placeHolder = DecorPlaceHolder(requireContext())

            when (lazyLoadMode) {
                LazyLoadMode.IDLE, LazyLoadMode.RESUME -> {
                    placeHolder.viewConstructor = { createContentView() }
                    lazyLoad = true
                }

                else -> {
                    placeHolder.replaceView = createContentView()
                    lazyLoad = false
                }
            }

            this.placeHolder = placeHolder

            setupErrorView = createErrorView()
            setupLoadingView = createLoadingView()

            appBarStyle = getAppBarStyle()
        }
    }

    abstract fun createContentView(): View
    open fun createErrorView(): CreateDecorErrorView? = null
    open fun createLoadingView(): CreateDecorLoadingView? = null


    @Synchronized
    override fun startInitializeFlow() {
        if (initializeFlowOver) return
        performContentViewInitialize()
        clearInitializeFlowListeners()
        initializeFlowOver = true
    }

    @CallSuper
    override fun onResume() {
        super.onResume()

        if (lazyLoadMode != LazyLoadMode.NONE) {
            if (lazyLoadMode == LazyLoadMode.IDLE) {
                removeIdleHandler()
            }

            if (host != null && !initializeFlowOver) {
                decorView?.inflate()
                startInitializeFlow()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (lazyLoadMode == LazyLoadMode.IDLE) {
            removeIdleHandler()
        }
    }

    fun doOnLayout(action: (view: View) -> Unit) {
        decorView?.doOnLayout(action)
    }

    fun doOnPreDraw(action: (view: View) -> Unit) {
        decorView?.doOnPreDraw { action(it) }
    }

    open fun <V : View?> findViewById(id: Int): V? = decorView?.findViewById<V>(id)

    /**
     * 点击重试
     */
    protected open fun onRetryClick() {
    }

    @CallSuper
    override fun getContext(): Context? {
        setRealContext(super.getContext())
        return fitContext ?: super.getContext()
    }

    private fun setRealContext(context: Context?) {
        if (context == null) return
        val annotation = getAnnotation(FitStrategy::class)
        fitContext = if (annotation != null) {
            if (annotation.value != Config.fit().mode) {
                context.createResourceContext(annotation.value)
            } else {
                context
            }
        } else {
            context
        }
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val useContext = context
        return if (useContext != null && useContext != super.getContext()) {
            useContext.cloneLayoutInflater()
        } else {
            super.onGetLayoutInflater(savedInstanceState)
        }
    }

    private fun addIdleHandler() {
        Looper.myQueue().addIdleHandler(idleHandler.value)
    }

    private fun removeIdleHandler() {
        if (idleHandler.isInitialized()) Looper.myQueue().removeIdleHandler(idleHandler.value)
    }
}