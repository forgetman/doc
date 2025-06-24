package vector.app.decor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import sugar.ext.throwIfNull
import vector.app.androidview.R
import vector.app.appbar.AppBar
import vector.app.ext.asStyle
import vector.app.ext.view.ensureIdExist
import vector.app.ext.view.gone
import vector.app.ext.view.show
import vector.app.config.Config
import vector.app.ext.createResourceContext
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import java.lang.ref.WeakReference

enum class ViewState {
    NORMAL,
    LOADING,
    ERROR
}

internal typealias CreateDecorPlaceHolderView = () -> View
typealias CreateDecorErrorView = (Context) -> ErrorViewEx
typealias CreateDecorLoadingView = (Context) -> View

/**
 * [DecorView.placeHolder]的占位View
 */
class DecorPlaceHolder @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var replaceView: View? = null
    var viewConstructor: CreateDecorPlaceHolderView? = null

    private var viewRef: WeakReference<View>? = null
    internal val view: View?
        get() = viewRef?.get()

    init {
        visibility = GONE
        setWillNotDraw(true)
    }

    internal fun inflate(): View? {
        if (view != null) return view

        val viewParent = parent
        if (viewParent != null && viewParent is ViewGroup) {
            val v = replaceView ?: viewConstructor?.invoke() ?: throwIfNull("view can not be null")
            replaceSelfWithView(v, viewParent)
            viewRef = WeakReference(v)
            return v
        } else {
            throw IllegalStateException("viewParent is null or is not ViewGroup")
        }
    }

    internal fun inflate(parent: ViewGroup, params: ViewGroup.LayoutParams?): View? {
        if (view != null) return view

        val v = replaceView ?: viewConstructor?.invoke() ?: throwIfNull("view can not be null")
        if (params != null) {
            parent.addView(v, params)
        } else {
            parent.addView(v)
        }
        viewRef = WeakReference(v)
        return v
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(0, 0)
    }

    override fun getLayoutParams(): ViewGroup.LayoutParams? {
        return view?.layoutParams ?: super.getLayoutParams()
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        view?.setLayoutParams(params) ?: super.setLayoutParams(params)
    }

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas) {
    }

    override fun dispatchDraw(canvas: Canvas) {}

    override fun setVisibility(visibility: Int) {
        if (viewRef != null) {
            val view = viewRef?.get()
            if (view != null) {
                view.visibility = visibility
            } else {
                throw IllegalStateException("setVisibility called on un-referenced view")
            }
        } else {
            super.setVisibility(visibility)
            if (visibility == VISIBLE || visibility == INVISIBLE) {
                inflate()
            }
        }
    }

    private fun replaceSelfWithView(view: View, parent: ViewGroup) {
        val index = parent.indexOfChild(this)
        parent.removeViewInLayout(this)

        val layoutParams = super.getLayoutParams()
        if (layoutParams != null) {
            parent.addView(view, index, layoutParams)
        } else {
            parent.addView(view, index)
        }
    }
}

enum class AppBarStyle {
    LINEAR, // 和界面平级
    FLOATING // 悬浮在界面上层
}

/**
 * 布局里真正使用的DecorView, 用于替代原生的DecorView
 */
class DecorView private constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        fun create(context: Context, action: DecorView.() -> Unit): DecorView {
            val v = DecorView(context)
            action(v)

            val params = LayoutParamsFactory.viewGroup(MATCH_PARENT, MATCH_PARENT)
            if (!v.lazyLoad) {
                v.contentView = v.placeHolder?.inflate(v, params)
                v.placeHolder = null
            } else {
                v.addView(v.placeHolder, params)
            }

            return v
        }
    }

    var placeHolder: DecorPlaceHolder? = null
    internal var contentView: View? = null
    var appBarStyle: AppBarStyle? = null

    var viewState = ViewState.NORMAL
        set(value) {
            if (field == value) {
                return
            }

            field = value
            when (value) {
                ViewState.NORMAL -> {
                    contentView.show()
                    if (errorView.isInitialized()) errorView.value.gone()
                    if (loadingView.isInitialized()) loadingView.value.gone()
                }

                ViewState.LOADING -> {
                    loadingView.value.show()
                    contentView.gone()
                    if (errorView.isInitialized()) errorView.value.gone()
                }

                ViewState.ERROR -> {
                    errorView.value.show()
                    contentView.gone()
                    if (loadingView.isInitialized()) loadingView.value.gone()
                }
            }
        }

    /**
     * 延迟加载[AppBar], 如果页面不需要的话不会添加进父布局
     */
    private val appBarDelegate = lazy {
        // AppBar使用单独的context环境(和config保持一致), 避免因为页面的单独设置出现不一致的情况
        val newContext = context.createResourceContext()
        val bar = AppBar(newContext).apply {
            id = R.id.app_bar
        }
        addView(bar)

        newSet {
            val viewId = bar.id
            constrainWidth(viewId, ConstraintSet.MATCH_CONSTRAINT)
            constrainHeight(viewId, ConstraintSet.WRAP_CONTENT)
            connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            if (!isFloating()) {
                resetConstraintWhenCreateAppBar(contentView, viewId, this)
                if (loadingView.isInitialized()) {
                    resetConstraintWhenCreateAppBar(loadingView.value, viewId, this)
                }
                if (errorView.isInitialized()) {
                    resetConstraintWhenCreateAppBar(errorView.value, viewId, this)
                }
            }
        }.applyToWithoutCustom(this@DecorView)

        bar
    }

    val appBar: AppBar by appBarDelegate

    var setupErrorView: CreateDecorErrorView? = null // 外部设置的errorView
    private var errorView = lazy {
        val v = setupErrorView?.invoke(context)
            ?: Config.app().errorConstructor?.invoke(context)
            ?: ErrorViewImpl(context)
        add(v)
        v.listener = errorClickListener
        v
    }

    var setupLoadingView: CreateDecorLoadingView? = null // 外部设置的loadingView
    private var loadingView = lazy {
        val v = setupLoadingView?.invoke(context)
            ?: Config.app().loadingConstructor?.invoke(context)
            ?: LoadingView(context)
        add(v)
        v
    }

    var errorClickListener: OnClickListener? = null
    var lazyLoad = false

    init {
        layoutParams = LayoutParamsFactory.viewGroup(MATCH_PARENT, MATCH_PARENT)
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)

        if (loadingView.isInitialized()) loadingView.value.setBackgroundColor(color)
        if (errorView.isInitialized()) errorView.value.setBackgroundColor(color)
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)

        if (loadingView.isInitialized()) loadingView.value.background = background
        if (errorView.isInitialized()) errorView.value.background = background
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun setBackgroundDrawable(background: Drawable?) {
        super.setBackgroundDrawable(background)

        if (loadingView.isInitialized()) loadingView.value.setBackgroundDrawable(background)
        if (errorView.isInitialized()) errorView.value.setBackgroundDrawable(background)
    }

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)

        if (loadingView.isInitialized()) loadingView.value.setBackgroundResource(resid)
        if (errorView.isInitialized()) errorView.value.setBackgroundResource(resid)
    }

    fun inflate() {
        if (lazyLoad) {
            contentView = placeHolder?.inflate()
            if (appBarDelegate.isInitialized() && !isFloating()) {
                newSet {
                    resetConstraintWhenCreateAppBar(contentView, appBar.id, this)
                }.applyToWithoutCustom(this)
            }
            placeHolder = null
        }
    }

    private fun isFloating(): Boolean {
        val state = appBarStyle ?: AppBarStyle.LINEAR
        return state == AppBarStyle.FLOATING
    }

    private fun add(v: View) {
        addView(v)
        newSet {
            v.ensureIdExist()
            val id = v.id
            setConstraint(id, this)
        }.applyToWithoutCustom(this)
    }

    private fun setConstraint(viewId: Int?, set: ConstraintSet) {
        viewId ?: return
        set.asStyle {
            constrainSizeMatch(viewId)
            withRule(viewId) {
                start toStart ConstraintSet.PARENT_ID
                end toEnd ConstraintSet.PARENT_ID
                if (appBarDelegate.isInitialized() && !isFloating()) {
                    top toBottom appBar.id
                } else {
                    top toTop ConstraintSet.PARENT_ID
                }
                bottom toBottom ConstraintSet.PARENT_ID
            }
        }
    }

    private fun resetConstraintWhenCreateAppBar(view: View?, appBarId: Int, set: ConstraintSet) {
        val viewId = view?.id ?: return
        set.asStyle {
            constrainSizeMatch(viewId)
            withRule(viewId) {
                start toStart ConstraintSet.PARENT_ID
                end toEnd ConstraintSet.PARENT_ID
                top toBottom appBarId
                bottom toBottom ConstraintSet.PARENT_ID
            }
        }
    }

    private fun newSet(action: ConstraintSet.() -> Unit): ConstraintSet {
        val set = ConstraintSet().apply {
            // 复制之前的配置, 防止其他的已添加的view属性丢失
            ensureAllChildrenHasId()
        }
        action(set)
        return set
    }

    private fun ensureAllChildrenHasId() {
        for (i in 0 until childCount) {
            getChildAt(i).ensureIdExist()
        }
    }
}

