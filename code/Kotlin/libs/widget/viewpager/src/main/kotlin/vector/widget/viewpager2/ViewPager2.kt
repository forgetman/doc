package vector.widget.viewpager2

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import sugar.ext.cast
import sugar.util.setFieldValue
import vector.app.config.Config
import vector.app.config.ViewPagerConfig
import vector.app.ext.view.findViewByType
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.widget.scrollable.adapter.ItemAdapter
import vector.widget.scrollable.adapter.ItemCompare
import androidx.viewpager2.widget.ViewPager2 as XViewPager2

/**
 * @author yuansui
 * @since 2019-07-19
 */
@Suppress("unused")
open class ViewPager2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollableHost(context, attrs, defStyleAttr) {

    companion object {
        const val ORIENTATION_HORIZONTAL = ViewPager2.ORIENTATION_HORIZONTAL
        const val ORIENTATION_VERTICAL = ViewPager2.ORIENTATION_VERTICAL
        const val SCROLL_STATE_IDLE = ViewPager2.SCROLL_STATE_IDLE
    }

    /**
     * 禁止通过override的方式来操作delegate的对象, 因为在父类构造阶段时会调用, 这时delegate未初始化
     */
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    private annotation class OverrideForbidden

    // 实际使用官方实例作为代理
    private val delegate: ViewPager2 = ViewPager2(context, attrs, defStyleAttr)

    private val tmpContainerRect = Rect()
    private val tmpChildRect = Rect()

    var mediator: TabLayoutMediator? = null

    var smoothScroll = true

    var isScrollable: Boolean
        get() = delegate.isUserInputEnabled
        set(value) {
            delegate.isUserInputEnabled = value
        }

    val scrollState get() = delegate.scrollState

    var itemComparison: ItemCompare = ItemCompare.RANGE_CHANGED
        set(value) {
            field = value
            adapter.cast<ItemAdapter> {
                it.itemCompare = value
            }
        }

    @ViewPager2.OffscreenPageLimit
    var offscreenPageLimit: Int
        get() = delegate.offscreenPageLimit
        set(value) {
            if (value == 0 || delegate.offscreenPageLimit == value) return
            delegate.offscreenPageLimit = value
        }

    var orientation: Int
        get() = delegate.orientation
        set(value) {
            delegate.orientation = value
        }

    var accessibilityProvider: AccessibilityProvider? = null
    var adapter: RecyclerView.Adapter<*>?
        get() = delegate.adapter
        set(value) {
            val currentAdapter = delegate.adapter
            accessibilityProvider?.onDetachAdapter(currentAdapter)

            delegate.adapter = value
            accessibilityProvider?.onAttachAdapter(value)
        }

    var currentItem: Int
        get() = delegate.currentItem
        set(value) {
            setCurrentItem(value, smoothScroll)
        }

    var scrollMode: Int = OVER_SCROLL_ALWAYS
        get() {
            val find = delegate.findViewByType<RecyclerView>()
            return find?.overScrollMode ?: OVER_SCROLL_ALWAYS
        }
        set(value) {
            field = value
            delegate.findViewByType<RecyclerView>()?.let {
                it.overScrollMode = value
            }
        }

    @OverrideForbidden
    override fun setOverScrollMode(overScrollMode: Int) {
        super.setOverScrollMode(overScrollMode)
    }

    val config: ViewPagerConfig
        get() = Config.viewPager()

    init {
        delegate.id = generateViewId()
        delegate.layoutParams = LayoutParamsFactory.frame(MATCH_PARENT, MATCH_PARENT)
        @Suppress("LeakingThis")
        attachViewToParent(delegate, 0, delegate.layoutParams)

        /**
         * FIXME: 暂时解决page过多时, 来回切换会显示空白的问题
         */
        isSaveEnabled = false

        scrollMode = config.overScrollMode
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean?) {
        if (adapter == null) {
            /**
             * FIXME: 需要通过反射设置mPendingCurrentItem
             * viewPager2内部有完整的mPendingCurrentItem机制但是没有提供任何可以设置的方法, 可能是alpha版的缘故, 暂时用反射解决
             */
            delegate.setPendingCurrentItem(item)
        } else {
            if (delegate.isFakeDragging) {
                delegate.endFakeDrag()
            }
            runCatching {
                // FIXME: 应该要监听fake drag end的滑动状态, 但是目前没有提供, 暂时用try catch解决
                // 当fakeDragging的时候会抛出IllegalStateException
                delegate.setCurrentItem(item, smoothScroll ?: this.smoothScroll)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChild(delegate, widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = delegate.measuredWidth
        val height = delegate.measuredHeight

        // 参考的ViewPager2的源码, 不然会出现一段空白区域
        tmpContainerRect.left = paddingStart
        tmpContainerRect.right = r - l - paddingEnd
        tmpContainerRect.top = paddingTop
        tmpContainerRect.bottom = b - t - paddingBottom

        Gravity.apply(Gravity.TOP or Gravity.START, width, height, tmpContainerRect, tmpChildRect)
        delegate.layout(
            tmpChildRect.left,
            tmpChildRect.top,
            tmpChildRect.right,
            tmpChildRect.bottom
        )
    }

    @OverrideForbidden
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
    }

    /**
     * [android.view.View.setPadding]
     */
    fun setInnerPadding(start: Int, top: Int, end: Int, bottom: Int) {
        recyclerView?.setPadding(start, top, end, bottom)
    }

    /**
     * [android.view.View.setPadding]
     */
    fun setInnerPaddingHorizontal(padding: Int) {
        recyclerView?.updatePadding(left = padding, right = padding)
    }

    @OverrideForbidden
    override fun setClipToPadding(clipToPadding: Boolean) {
        super.setClipToPadding(clipToPadding)
    }

    /**
     * [RecyclerView.setClipToPadding]
     */
    fun setInnerClipToPadding(clipToPadding: Boolean) {
        recyclerView?.clipToPadding = clipToPadding
    }

    /**
     * [RecyclerView.getClipToPadding]
     */
    fun getInnerClipToPadding(): Boolean {
        return recyclerView?.clipToPadding == true
    }

    fun registerOnPageChangeCallback(callback: ViewPager2.OnPageChangeCallback) {
        delegate.registerOnPageChangeCallback(callback)
    }

    fun unregisterOnPageChangeCallback(callback: ViewPager2.OnPageChangeCallback) {
        delegate.unregisterOnPageChangeCallback(callback)
    }

    fun setPageTransformer(pageTransformer: ViewPager2.PageTransformer?) {
        delegate.setPageTransformer(pageTransformer)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mediator?.attach()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mediator?.detach()
    }

    abstract class AccessibilityProvider {
        open fun onDetachAdapter(oldAdapter: RecyclerView.Adapter<*>?) {}
        open fun onAttachAdapter(newAdapter: RecyclerView.Adapter<*>?) {}
    }

    fun beginFakeDrag(): Boolean = delegate.beginFakeDrag()
    fun fakeDragBy(offsetPxFloat: Float): Boolean = delegate.fakeDragBy(offsetPxFloat)
    fun endFakeDrag(): Boolean = delegate.endFakeDrag()

    private val recyclerView: RecyclerView? by lazy {
        try {
            val field = delegate.javaClass.getDeclaredField("mRecyclerView").apply {
                isAccessible = true
            }
            field.get(delegate) as RecyclerView
        } catch (e: NoSuchFileException) {
            delegate.findViewByType()
        }
    }

    // <editor-fold defaultstate = expanded" desc = "反射field">
    private val scrollEventAdapter by lazy {
        val field = delegate.javaClass.getDeclaredField("mScrollEventAdapter").apply {
            isAccessible = true
        }
        field.get(delegate)
    }

    private val isFakeDraggingMethod by lazy {
        scrollEventAdapter.javaClass.getDeclaredMethod("isFakeDragging").apply {
            isAccessible = true
        }
    }

    private val notifyEndFakeDragMethod by lazy {
        scrollEventAdapter.javaClass.getDeclaredMethod("notifyEndFakeDrag").apply {
            isAccessible = true
        }
    }

    private val snapToPageMethod by lazy {
        delegate.javaClass.getDeclaredMethod("snapToPage").apply {
            isAccessible = true
        }
    }
    // </editor-fold>

    /**
     * 默认的endDrag中的速度计算在部分设备上存在一些问题，提供一个自定义的endDrag方法
     * @param velocity 速度
     */
    fun endFakeDrag(velocity: Float): Boolean {
        if (isFakeDraggingMethod.invoke(scrollEventAdapter) != true) {
            return false
        }

        notifyEndFakeDragMethod.invoke(scrollEventAdapter)

        if (recyclerView?.fling(velocity.toInt(), 0) == false) {
            snapToPageMethod.invoke(delegate)
        }

        return true
    }

    /**
     * [RecyclerView.setHasFixedSize]
     */
    fun setItemViewCacheSize(size: Int) {
        recyclerView?.setItemViewCacheSize(size)
    }

    /**
     * [RecyclerView.setItemAnimator]
     */
    fun setItemAnimator(animator: RecyclerView.ItemAnimator?) {
        recyclerView?.itemAnimator = animator
    }
}

private fun XViewPager2.setPendingCurrentItem(item: Int) {
    setFieldValue("mPendingCurrentItem", item)
}