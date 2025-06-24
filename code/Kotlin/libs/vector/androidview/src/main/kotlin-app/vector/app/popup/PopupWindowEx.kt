package vector.app.popup

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.FloatRange
import sugar.ext.NoArgBlock
import sugar.ext.getAnnotation
import vector.Constants
import vector.os.lifecycle.LifecycleEventDispatcher
import vector.os.lifecycle.LifecycleEventOwner
import vector.app.UIHost
import vector.app.delegate.InitializeDelegate
import vector.app.delegate.InitializeInitializeDelegateImpl
import vector.app.delegate.performContentViewInitialize
import vector.app.delegate.performDataInitialize
import vector.app.delegate.performSystemBarInitialize
import vector.app.ext.getStrategyInflater
import vector.app.ext.inflateSpace
import vector.ext.postRunnable
import vector.app.fitter.FitStrategy
import vector.app.os.Dimension
import vector.util.MATCH_PARENT
import vector.app.util.Screen
import vector.util.WRAP_CONTENT
import androidx.core.graphics.drawable.toDrawable

/**
 * 背景变暗方式
 */
sealed class DimMode(@FloatRange(from = 0.0, to = 1.0) internal val amount: Float) {
    data object Disable : DimMode(0f)
    class Normal(@FloatRange(from = 0.0, to = 1.0) amount: Float) : DimMode(amount)
    class FullScreen(@FloatRange(from = 0.0, to = 1.0) amount: Float) : DimMode(amount)
}

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class PopupWindowEx(context: Context?) :
    UIHost,
    InitializeDelegate by InitializeInitializeDelegateImpl(),
    LifecycleEventOwner {

    companion object {
        private const val DEFAULT_DIM_AMOUNT = 0.3f
    }

    var dimMode: DimMode = DimMode.Normal(DEFAULT_DIM_AMOUNT)

    private var dimWindow: PopupWindow? = null

    private var onDismissListener: PopupWindow.OnDismissListener? = null

    lateinit var contentView: View

    override val uiView: View
        get() = contentView

    val isShowing: Boolean
        get() = window.isShowing

    open val width: Int
        get() = WRAP_CONTENT

    open val height: Int
        get() = WRAP_CONTENT

    var isOutsideTouchable: Boolean
        get() = window.isOutsideTouchable
        private set(value) {
            isTouchable = value
            window.isOutsideTouchable = value
        }

    var isFocusable: Boolean
        get() = window.isFocusable
        private set(value) {
            window.isFocusable = value
        }

    var isTouchable: Boolean
        get() = window.isTouchable
        private set(value) {
            window.isTouchable = value
        }

    private val inflater: LayoutInflater by lazy {
        val annotation = getAnnotation(FitStrategy::class)
        this.context.getStrategyInflater(annotation)
    }

    @Suppress("LeakingThis")
    final override val dispatcher: LifecycleEventDispatcher = LifecycleEventDispatcher(this)

    val context: Context
    private lateinit var window: PopupWindow

    init {
        if (context == null) {
            throw NullPointerException("context can not be null")
        } else {
            this.context = context
        }

        dispatcher.postOnCreate()
    }

    override fun onCreate() {
        contentView = createContentView(inflater)

        window = PopupWindow(contentView, width, height, true)

        window.setOnDismissListener {
            dimWindow?.dismiss()
            dispatcher.postOnDestroy()
            onDismissListener?.onDismiss()
        }

        // 默认习惯点击外部可以消失
        setTouchOutsideDismissEnabled(true)

        startInitializeFlow()
    }

    final override fun startInitializeFlow() {
        performDataInitialize()
        performSystemBarInitialize()
        performContentViewInitialize()

        clearInitializeFlowListeners()
    }

    abstract fun createContentView(layoutInflater: LayoutInflater): View

    fun dismiss() {
        window.dismiss()
    }

    fun onDismiss(callback: NoArgBlock) {
        onDismissListener = PopupWindow.OnDismissListener { callback() }
    }

    /**
     * 设置点击外部空白处是否自动消失
     *
     * @param enabled 是否消失
     */
    fun setTouchOutsideDismissEnabled(enabled: Boolean) {
        isOutsideTouchable = enabled
        isFocusable = enabled
        window.setBackgroundDrawable(
            if (enabled) {
                Color.TRANSPARENT.toDrawable()
            } else {
                null
            }
        )
    }

    private fun getDimWindow(anchor: View): PopupWindow {
        val oldDimWindow = dimWindow
        if (oldDimWindow != null) return oldDimWindow

        val popup = PopupWindow(this.context).apply {
            contentView = this@PopupWindowEx.context.inflateSpace(Dimension.Px(1))
            width = MATCH_PARENT
            height = if (dimMode is DimMode.Normal) {
                window.getMaxAvailableHeight(anchor)
            } else {
                MATCH_PARENT
            }
            isFocusable = false
            isTouchable = false
        }
        setDimAmount(popup)

        dimWindow = popup

        return popup
    }

    /**
     * 设置背景变暗
     */
    private fun setDimAmount(popup: PopupWindow) {
        when (val mode = dimMode) {
            is DimMode.Disable -> {
                popup.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            }

            else -> {
                popup.apply {
                    val alpha = (mode.amount * Constants.ALPHA_MAX).toInt()
                    val dimColor = Color.argb(alpha, 0, 0, 0)
                    this.setBackgroundDrawable(dimColor.toDrawable())
                }
            }
        }
    }

    private fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        val mode: Int = if (measureSpec == WRAP_CONTENT) {
            View.MeasureSpec.UNSPECIFIED
        } else {
            View.MeasureSpec.EXACTLY
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode)
    }

    /**
     * @param anchor the view on which to pin the popup window
     * @param xoff A horizontal offset from the anchor in pixels
     * @param yoff A vertical offset from the anchor in pixels
     */
    fun showAsDropDown(
        anchor: View, xoff: Int? = null, yoff: Int = 0, gravity: Int = Gravity.NO_GRAVITY
    ) {
        // make sure run it after onCreate
        postRunnable {
            if (window.isShowing) return@postRunnable

            var xoff1 = 0
            var gravity1: Int = gravity

            if (xoff != null) {
                contentView.measure(makeDropDownMeasureSpec(width), makeDropDownMeasureSpec(height))
                val w = contentView.measuredWidth

                when (gravity) {
                    Gravity.END -> {
                        /**
                         * 思路: 判断图标的位置
                         * 1. 在右边, 判断是否有足够的空间放下contentView
                         *    a. 没有, 改为[Gravity.START]方式, 并算出以此方式把contentView绘制到正好贴边的坐标X
                         *    b. 有, 一切照旧
                         * 2. 在左边, 一切照旧
                         */
                        // 获取anchor坐标
                        val array = IntArray(2)
                        anchor.getLocationInWindow(array)
                        val anchorX = array[0]
                        // 判断anchor位置
                        val anchorOnRight = anchorX > Screen.width / 2
                        if (anchorOnRight) {
                            val rightRemain = Screen.width - anchorX - anchor.width
                            if (rightRemain < w) {
                                gravity1 = Gravity.START
                                val xAlignEnd = -w + anchor.width + rightRemain
                                xoff1 = xoff + xAlignEnd
                            }
                        }
                    }
                }
            }

            when (dimMode) {
                DimMode.Disable -> {
                    // 不弹出
                }

                is DimMode.Normal -> {
                    getDimWindow(anchor).showAsDropDown(anchor, 0, 0, Gravity.NO_GRAVITY)
                }

                is DimMode.FullScreen -> {
                    getDimWindow(anchor).apply {
                        isClippingEnabled = false
                        showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
                    }
                }
            }

            window.showAsDropDown(anchor, xoff1, yoff, gravity1)
            postToResume()
        }
    }

    /**
     * @param parent  a parent view to get the [View.getWindowToken] token from
     * @param gravity the gravity which controls the placement of the popup window
     * @param x       the popup's x location offset
     * @param y       the popup's y location offset
     */
    fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        postRunnable {
            if (window.isShowing) return@postRunnable

            when (dimMode) {
                DimMode.Disable -> {
                    // 不弹出
                }

                is DimMode.Normal -> {
                    getDimWindow(parent).apply {
                        showAtLocation(parent, gravity, x, y)
                    }
                }

                is DimMode.FullScreen -> {
                    getDimWindow(parent).apply {
                        isClippingEnabled = false
                        showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0)
                    }
                }
            }

            window.showAtLocation(parent, gravity, x, y)
            postToResume()
        }
    }

    /**
     * @param anchor the popup's anchor view
     * @param xoff x offset from the view's left edge
     * @param yoff y offset from the view's bottom edge
     * @param width the new width in pixels, must be >= 0 or -1 to ignore
     * @param height the new height in pixels, must be >= 0 or -1 to ignore
     */
    fun update(anchor: View, xoff: Int, yoff: Int, width: Int, height: Int) {
        postRunnable {
            if (!window.isShowing) return@postRunnable

            when (dimMode) {
                DimMode.Disable -> {
                    // 不弹出
                }

                is DimMode.Normal -> {
                    getDimWindow(anchor).update(anchor, xoff, yoff, width, height)
                }

                is DimMode.FullScreen -> {
                    getDimWindow(anchor).apply {
                        isClippingEnabled = false
                        update(anchor, 0, 0, MATCH_PARENT, MATCH_PARENT)
                    }
                }
            }

            window.update(anchor, xoff, yoff, width, height)
        }
    }

    class Builder {
        var context: Context? = null
        var view: View? = null
        var dimMode: DimMode? = null

        fun build(): PopupWindowEx {
            checkNotNull(context) {
                "builder context can not be null"
            }
            return object : PopupWindowEx(context) {

                override fun createContentView(layoutInflater: LayoutInflater): View {
                    return checkNotNull(uiView) {
                        "builder view can not be null"
                    }
                }

                override fun initializeContentView() {
                    this@Builder.dimMode?.let {
                        dimMode = it
                    }
                }
            }
        }
    }

    private fun postToResume() {
        dispatcher.postOnStart()
        dispatcher.postOnResume()
    }
}

/**
 * 创建简易使用默认适配模式的弹窗
 */
fun buildPopupWindow(action: (PopupWindowEx.Builder) -> Unit): PopupWindowEx {
    val builder = PopupWindowEx.Builder()
    action(builder)
    return builder.build()
}
