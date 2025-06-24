@file:Suppress("unused")

package vector.app.ext.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import compat.inputmethod.InputMethodCompat
import sugar.ext.SdkInt
import sugar.ext.cast
import sugar.ext.isSdkAtLeast
import vector.app.androidview.R
import vector.app.ext.NO_GETTER
import vector.app.ext.applyCanvas
import vector.app.ext.noGetter
import java.util.ArrayDeque
import kotlin.reflect.KClass

private typealias ViewSimpleAction = (View) -> Unit

fun interface OnWindowInsetsChangedListener {
    fun onChanged(
        v: View,
        insets: WindowInsetsCompat,
        initialPadding: RelativePadding
    ): WindowInsetsCompat
}

/**
 * 关闭view层级的硬件加速
 * 部分2D方法在应用开启硬件加速后会失效或崩溃, 需要关闭加速, 如
 *
 * [Canvas.clipPath]
 * [Canvas.drawPicture]
 * [Canvas.drawTextOnPath]
 * [Canvas.drawVertices]
 *
 * [Paint.setLinearText]
 * [Paint.setMaskFilter]
 */
fun View.disableHardwareAcc() {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
}

/**
 * 绑定前景
 */
fun View.bindForeground(old: Drawable?, new: Drawable?): Boolean {
    if (old != new) {
        old?.let {
            it.callback = null
            unscheduleDrawable(it)
        }

        if (new != null) {
            setWillNotDraw(false)
            new.callback = this
            if (new.isStateful) {
                new.state = drawableState
            }
        } else {
            setWillNotDraw(true)
        }

        requestLayout()
        invalidate()

        return true
    }

    return false
}

fun View?.show() {
    if (this != null) {
        isVisible = true
    }
}

fun View?.gone() {
    if (this != null) {
        isVisible = false
    }
}

fun View?.hide() {
    if (this != null) {
        isInvisible = true
    }
}

fun View.setHeight(height: Int) {
    val params = layoutParams ?: return
    params.height = height
    layoutParams = params
}

fun View.setWidth(width: Int) {
    val params = layoutParams ?: return
    params.width = width
    layoutParams = params
}

private class DebounceClickListener(
    private val interval: Long?,
    private val action: (View) -> Unit
) : View.OnClickListener {

    companion object {
        const val DEFAULT_INTERVAL = 500L
    }

    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > (interval ?: DEFAULT_INTERVAL)) {
            lastClickTime = currentTime
            action(v)
        }
    }
}

/**
 * 去抖动点击
 * @param interval 抖动间隔
 */
fun <T : View> T?.setOnDebounceClickListener(interval: Long? = null, action: (View) -> Unit) {
    this?.setOnClickListener(DebounceClickListener(interval, action))
}

fun View.removeOnClick() {
    setOnClickListener(null)
    isClickable = false
}

fun <T : View> T?.onLongClick(block: (View) -> Boolean) {
    this?.setOnLongClickListener { block(it) }
}

fun View.removeOnLongClick() {
    setOnLongClickListener(null)
}

fun View.requestFocus(focus: Boolean) {
    when (focus) {
        true -> {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }

        false -> clearFocus()
    }
}

fun View.onFocusChanged(block: (hasFocus: Boolean) -> Unit) {
    setOnFocusChangeListener { _, hasFocus ->
        block(hasFocus)
    }
}

/**
 * Performs the given action when the view tree is about to be drawn.
 */
inline fun View.doOnPreDraw(always: Boolean = false, crossinline action: (view: View) -> Unit) {
    val vto = viewTreeObserver
    if (!vto.isAlive) return
    vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            action(this@doOnPreDraw)
            if (!always) {
                when {
                    vto.isAlive -> vto.removeOnPreDrawListener(this)
                    else -> viewTreeObserver.removeOnPreDrawListener(this)
                }
            }
            return true
        }
    })
}

inline fun View.doOnDraw(always: Boolean = false, crossinline action: (view: View) -> Unit) {
    if (always) {
        val vto = viewTreeObserver
        if (!vto.isAlive) return
        vto.addOnDrawListener { action(this@doOnDraw) }
    } else {
        OneShotOnDrawListener.add(this) {
            action(this)
        }
    }
}

/**
 * 转换到bitmap
 * bitmap的宽高和layout等同
 */
fun View.toBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    if (!isLaidOut()) {
        throw IllegalStateException("View needs to be laid out before calling toBitmap()")
    }
    return Bitmap.createBitmap(width, height, config).applyCanvas(::draw)
}

fun View.margin(start: Int? = null, top: Int? = null, end: Int? = null, bottom: Int? = null) {
    val params = layoutParams
    if (params is ViewGroup.MarginLayoutParams) {
        if (start != null) {
            params.marginStart = start
        }
        if (top != null) {
            params.topMargin = top
        }
        if (end != null) {
            params.marginEnd = end
        }
        if (bottom != null) {
            params.bottomMargin = bottom
        }
        layoutParams = params
    }
}

inline fun <reified T : View> View.findViewByType(): T? = findViewByClass(T::class)

@Suppress("UNCHECKED_CAST")
fun <T : View> View.findViewByClass(cls: KClass<T>): T? {
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            val c = getChildAt(i)
            if (cls.isInstance(c)) return c as T
            if (c is ViewGroup) {
                val find: T? = c.findViewByClass(cls)
                if (find != null) return find
            }
        }
    } else if (cls.isInstance(this)) {
        return this as T
    }
    return null
}

fun View.findIndex(@IdRes id: Int): Int {
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            val c = getChildAt(i)
            if (c.id == id) return i
        }
    }

    return -1
}

/**
 * 通过广度遍历所有的view
 * 非递归,利用队列的数据结构
 * <p>
 *     递归寻找所有匹配类型的view
 *     PS: 原本打算使用 reified 机制, 直接 is 判断, 结果kotlin限制 reified 声明的不能使用递归
 *     fun <T : View> View.findAll(clazz: KClass<T>): List<T> {
 *          val list = mutableListOf<T>()
 *          if (this is ViewGroup) {
 *              forEachChild {
 *                  @Suppress("UNCHECKED_CAST")
 *                  when {
 *                      it::class == clazz -> list.add(it as T)
 *                      it::class.isSubclassOf(clazz) -> list.add(it as T)
 *                      it is ViewGroup -> list.addAll(it.findAll(clazz))
 *                  }
 *              }
 *          }
 *          return list
 *     }
 * </p>
 */
inline fun <reified T : View> View.findAll(): List<T> {
    return findAll(this, T::class.java)
}

fun <T> findAll(target: View, viewCls: Class<T>): List<T> {
    val views = mutableListOf<T>()

    val deque = ArrayDeque<View>()
    deque.addLast(target)

    while (deque.isNotEmpty()) {
        val view = deque.first
        if (view is ViewGroup) {
            view.forEach {
                deque.addLast(it)
            }
        }
        // 不用else,如T是viewGroup
        if (viewCls.isAssignableFrom(view::class.java)) {
            @Suppress("UNCHECKED_CAST")
            views.add(view as T)
        }

        deque.pollFirst()
    }

    return views
}

@Suppress("UNCHECKED_CAST")
fun View.doOnApplyWindowInsets(always: Boolean = true, l: OnWindowInsetsChangedListener) {
    // Create a snapshot of the view's padding state.
    val initialPadding = RelativePadding(
        this.paddingStart,
        this.paddingTop,
        this.paddingEnd,
        this.paddingBottom
    )
    // Set an actual OnApplyWindowInsetsListener which proxies to the given callback, also passing
    // in the original padding state.
    val key = R.id.view_window_insets
    val key2 = R.id.view_window_insets_list

    var old = this.getTag(key) as? OnApplyWindowInsetsListener?
    val listeners: MutableList<OnWindowInsetsChangedListener>?
    if (old == null) {
        listeners = mutableListOf()
        old = OnApplyWindowInsetsListener { v, insets ->
            var newInsets: WindowInsetsCompat = insets
            listeners.forEach {
                newInsets = it.onChanged(v, newInsets, RelativePadding(initialPadding))
            }
            if (!always) {
                listeners.remove(l)
            }
            newInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(this, old)

        setTag(key, old)
        setTag(key2, listeners)
    } else {
        listeners = getTag(key2) as? MutableList<OnWindowInsetsChangedListener>?
    }

    listeners?.add(l)

    // Request some insets.
    requestApplyInsetsWhenAttached(this)
}

/**
 * Requests that insets should be applied to this view once it is attached.
 */
internal fun requestApplyInsetsWhenAttached(view: View) {
    if (view.isAttachedToWindow()) {
        // We're already attached, just request as normal.
        ViewCompat.requestApplyInsets(view)
    } else {
        // We're not attached to the hierarchy, add a listener to request when we are.
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(v)
            }

            override fun onViewDetachedFromWindow(v: View) {}
        })
    }
}

class RelativePadding {
    var start: Int
    var top: Int
    var end: Int
    var bottom: Int

    constructor(start: Int, top: Int, end: Int, bottom: Int) {
        this.start = start
        this.top = top
        this.end = end
        this.bottom = bottom
    }

    constructor(other: RelativePadding) {
        start = other.start
        top = other.top
        end = other.end
        bottom = other.bottom
    }

    /** Applies this relative padding to the view.  */
    fun applyToView(view: View?) {
        view?.setPaddingRelative(start, top, end, bottom)
    }
}

fun View?.ensureIdExist() {
    this ?: return
    if (id == View.NO_ID) {
        id = View.generateViewId()
    }
}

fun View.toLayoutInflater(): LayoutInflater = LayoutInflater.from(context)

var View.scale: Float
    @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
    set(value) {
        scaleX = value
        scaleY = value
    }

interface OnMultiClickListener {
    fun onSingleClick(view: View) {}
    fun onDoubleClick(view: View) {}
    fun onLongPress(view: View) {}
}

@SuppressLint("ClickableViewAccessibility")
fun View.setOnMultiClickListener(
    singleClick: ViewSimpleAction? = null,
    doubleClick: ViewSimpleAction? = null,
    longPress: ViewSimpleAction? = null,
) {
    val listener = object : OnMultiClickListener {
        override fun onSingleClick(view: View) {
            singleClick?.invoke(view)
        }

        override fun onDoubleClick(view: View) {
            doubleClick?.invoke(view)
        }

        override fun onLongPress(view: View) {
            longPress?.invoke(view)
        }
    }

    val v = this
    val detectorListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            if (isSdkAtLeast(SdkInt.L_21)) {
                drawableHotspotChanged(e.x, e.y)
            }
            v.isPressed = true
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return if (doubleClick != null) {
                // 有双击事件, 不响应
                super.onSingleTapUp(e)
            } else {
                listener.onSingleClick(v)
                v.isPressed = false
                true
            }
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return if (doubleClick == null) {
                super.onSingleTapUp(e)
            } else {
                listener.onSingleClick(v)
                v.isPressed = false
                true
            }
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            listener.onDoubleClick(v)
            v.isPressed = false
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            listener.onLongPress(v)
            v.isPressed = false
        }
    }
    val detector = GestureDetector(context, detectorListener)
    detector.setIsLongpressEnabled(longPress != null)

    setOnTouchListener { _, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_CANCEL -> {
                v.isPressed = false
            }
        }
        detector.onTouchEvent(event)
    }
}

fun View.removeOnMultiClick() {
    setOnTouchListener(null)
}

fun View.removeFromParent() {
    parent.cast<ViewGroup> {
        it.removeView(this)
    }
}

fun View.showSoftInput(): Boolean {
    this.requestFocus()
    return InputMethodCompat.showSoftInput(context, this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideSoftInput(clearFocus: Boolean = true) {
    if (!InputMethodCompat.isActive(context, this)) return

    this.windowToken?.let {
        InputMethodCompat.hideSoftInputFromWindow(context, it, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    if (clearFocus) {
        this.clearFocus()
    }
}