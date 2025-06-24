package vector.app.activity

import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding
import sugar.ext.SdkInt
import sugar.ext.getAnnotation
import sugar.ext.isSdkAtLeast
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
import vector.app.ext.flatStatusBar
import vector.app.ext.view.doOnApplyWindowInsets
import vector.app.ext.view.doOnDraw
import vector.app.ext.view.doOnPreDraw
import vector.app.ext.view.findAll
import vector.app.ext.view.hideSoftInput
import vector.app.fitter.FitResources
import vector.app.fitter.FitStrategy
import vector.util.InjectUtil


/**
 * 不带mvvm模式的activity, 用于普通的业务
 */
abstract class SimpleActivityEx :
    AppCompatActivity(),
    UIHost,
    InitializeDelegate by InitializeInitializeDelegateImpl() {

    companion object {
        private const val FRAG_TAG = "android:fragments"
        private const val FRAG_SUPPORT_TAG = "android:support:fragments"
    }

    override val uiView: View?
        get() = decorView

    private lateinit var decorView: DecorView

    val appBar: AppBar by lazy {
        decorView.appBar
    }

    var viewState: ViewState? = null
        set(value) {
            if (value == null) return
            decorView.viewState = value
            field = value
        }

    private val fitResources: Resources by lazy {
        val annotation = getAnnotation(FitStrategy::class)
        FitResources.get(annotation?.value ?: Config.fit().mode, super.getResources())
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            this@SimpleActivityEx.handleOnBackPressed()
        }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFormat(PixelFormat.TRANSPARENT)

        // 清除所有fragment的保存
        savedInstanceState?.putParcelable(FRAG_TAG, null)
        savedInstanceState?.putParcelable(FRAG_SUPPORT_TAG, null)

        InjectUtil.bind(this)

        super.onCreate(savedInstanceState)

        decorView = onCreateDecorView()
        setContentView(decorView)

        adaptWindow()

        decorView.errorClickListener = View.OnClickListener {
            onRetryClick()
        }

        startInitializeFlow()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun startInitializeFlow() {
        performDataInitialize()
        performSystemBarInitialize()
        performContentViewInitialize()

        clearInitializeFlowListeners()
    }

    @CallSuper
    open fun onCreateDecorView(): DecorView {
        return DecorView.create(this) {
            placeHolder = DecorPlaceHolder(this@SimpleActivityEx).apply {
                replaceView = createContentView()
            }

            setupErrorView = createErrorView()
            setupLoadingView = createLoadingView()

            appBarStyle = getAppBarStyle()
        }
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

    open fun handleOnBackPressed() {
        supportFinishAfterTransition()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java", ReplaceWith("handleOnBackPressed()"))
    final override fun onBackPressed() {
        super.onBackPressed()
    }

    abstract fun createContentView(): View
    protected open fun createErrorView(): CreateDecorErrorView? = null
    protected open fun createLoadingView(): CreateDecorLoadingView? = null

    @CallSuper
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        InjectUtil.bind(this, intent)
    }

    protected fun doOnLayout(action: (view: View) -> Unit) {
        decorView.doOnLayout(action)
    }

    protected fun doOnPreDraw(action: (view: View) -> Unit) {
        decorView.doOnPreDraw(action = action)
    }

    protected fun doOnDraw(action: (view: View) -> Unit) {
        decorView.doOnDraw(action = action)
    }

    /**
     * 点击重试
     */
    protected open fun onRetryClick() {
    }

    override fun getLayoutInflater(): LayoutInflater {
        return LayoutInflater.from(this)
    }

    override fun getResources(): Resources {
        return fitResources
    }

    fun getFragment(index: Int) = supportFragmentManager.fragments.getOrNull(index)

    @CallSuper
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (enableHideKeyboardWhenFocusChanged()) {
            if (ev.action == MotionEvent.ACTION_DOWN) {
                findActiveEditText(currentFocus, ev)?.hideSoftInput(true)
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，
     */
    private fun findActiveEditText(currentFocus: View?, event: MotionEvent): EditText? {
        if (currentFocus is EditText) {
            val ets = uiView?.findAll<EditText>() ?: return null

            var ret = true
            val outLocation = intArrayOf(0, 0)

            for (item in ets) {
                item.getLocationInWindow(outLocation)
                val left = outLocation[0]
                val top = outLocation[1]
                val right = left + item.width
                val bottom = top + item.height
                val x = event.x.toInt()
                val y = event.y.toInt()
                if (x in left until right && y in top until bottom) {
                    ret = false
                    break
                }
            }

            if (ret) return currentFocus else null
        }
        return null
    }

    /**
     * 是否允许点击空白处收起键盘
     */
    open fun enableHideKeyboardWhenFocusChanged(): Boolean {
        return false
    }

    private fun adaptWindow() {
        if (Config.app().enableFlatBar) window.flatStatusBar()

        fun applyInsets() {
            decorView.doOnApplyWindowInsets { v, insets, _ ->
                val systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            or WindowInsetsCompat.Type.displayCutout()
                )
                val top = if (Config.app().enableFlatBar) 0 else systemBars.top
                v.updatePadding(top = top, bottom = systemBars.bottom)
                insets
            }
        }

        when {
            isSdkAtLeast(SdkInt.V_35) -> {
                if (Config.app().enableFlatBar) {
                    enableEdgeToEdge()
                    applyInsets()
                }
            }

            isSdkAtLeast(SdkInt.R_30) -> {
                // 适配api30的windows设置操作, 必须取消view本身的操作
                WindowCompat.setDecorFitsSystemWindows(window, false)
                applyInsets()
            }
        }
    }
}