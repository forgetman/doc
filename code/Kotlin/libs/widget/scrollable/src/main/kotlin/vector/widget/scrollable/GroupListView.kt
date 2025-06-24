package vector.widget.scrollable

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.withStyledAttributes
import vector.Constants
import vector.app.delegate.OnScrollCompatListener
import vector.util.DeviceUtil
import vector.widget.ext.obtainDrawable
import vector.widget.scrollable.adapter.GroupMultiAdapterEx
import vector.widget.scrollable.delegate.ScrollDelegate
import vector.widget.swiperefresh.delegate.LoadMore

typealias OnGroupClick = (v: View, groupPosition: Int, id: Long) -> Boolean
typealias OnChildClick = (v: View, groupPosition: Int, childPosition: Int, id: Long) -> Boolean

class GroupListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ExpandableListViewEx(context, attrs, defStyleAttr), ScrollDelegate {

    init {
        setAttrs(this)
    }

    /**
     * FIXME: 直接使用[androidx.appcompat.view.ContextThemeWrapper]来设置的话, [ExpandableListViewEx]无法读取到里面的属性, 原因未知
     * 暂时使用自定义属性的方式来解决(前提是开放了代码设置的方法)
     */
    @SuppressLint("CustomViewStyleable")
    private fun setAttrs(v: ExpandableListViewEx, attrs: AttributeSet? = null) {
        val styleContext = ContextThemeWrapper(context, R.style.View_List_Group)

        styleContext.withStyledAttributes(
            attrs,
            R.styleable.LibsWidgetScrollableGroupListView
        ) {
            obtainDrawable(
                R.styleable.LibsWidgetScrollableGroupListView_android_groupIndicator,
                styleContext.theme
            ) { drawable ->
                v.setGroupIndicator(drawable)
            }

            obtainDrawable(
                R.styleable.LibsWidgetScrollableGroupListView_android_childDivider,
                styleContext.theme
            ) { drawable ->
                v.setChildDivider(drawable)
            }
        }
    }

    var adapter: GroupMultiAdapterEx<*, *, *>? = null
        set(value) {
            if (value == null || field == value) return

            field?.unregisterDataSetObserver(dataSetObserver)

            setAdapter(value)
            value.registerDataSetObserver(dataSetObserver)

            field = value
        }

    // 是否加载完数据后展开所有组
    var expandAll = true

    private val dataSetObserver: DataSetObserver by lazy {
        object : DataSetObserver() {
            override fun onChanged() {
                if (expandAll) expandAllGroups() else collapseGroupAllGroups()
                // TODO: group的empty footer功能缺失
//                if (adapter?.groupCount == 0) {
//                    changeEmptyFooterVisibility(true)
//                } else {
//                    changeEmptyFooterVisibility(false)
//                }
            }
        }
    }

    /**
     * 是否正在加载更多
     */
    private var isLoadingMore: Boolean = false
    private var loadMoreListener: LoadMore.Listener? = null

    private var scrollListeners: MutableList<OnScrollCompatListener>? = null
    private val innerOnScrollListener by lazy(LazyThreadSafetyMode.NONE) {
        object : OnScrollListener {
            var firstVisibleItem = 0
            var visibleItemCount = 0
            var totalItemCount = 0

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                scrollListeners?.forEach { it.onScrollStateChanged(view, scrollState) }

                /*
                 loadMore相关
                 */
                if (loadMoreListener == null) return
                if (isLoadingMore || scrollState != OnScrollListener.SCROLL_STATE_IDLE) return

                val total = visibleItemCount + firstVisibleItem
                if (DeviceUtil.brand == Constants.Brand.MEI_ZU) {
                    // 单独处理魅族的手机, 他们计算的visibleItemCount就是比正常的少一个, 奇葩
                    if (total == totalItemCount - 1) {
                        startLoadMore(LoadMore.State.LOADING)
                    }
                } else {
                    if (total == totalItemCount) {
                        startLoadMore(LoadMore.State.LOADING)
                    }
                }
            }

            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                scrollListeners?.forEach {
                    it.onScroll(
                        view,
                        firstVisibleItem,
                        visibleItemCount,
                        totalItemCount
                    )
                }

                this.firstVisibleItem = firstVisibleItem
                this.visibleItemCount = visibleItemCount
                this.totalItemCount = totalItemCount
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setOnScrollListener(innerOnScrollListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        setOnScrollListener(null)
    }

    fun setOnGroupClickListener(block: OnGroupClick) {
        setOnGroupClickListener { _, v, groupPosition, id ->
            block(v, groupPosition, id)
        }
    }

    fun setOnChildClickListener(block: OnChildClick) {
        setOnChildClickListener { _, v, groupPosition, childPosition, id ->
            block(v, groupPosition, childPosition, id)
        }
    }

    /**
     * 展开全部
     */
    private fun expandAllGroups() {
        val size = adapter?.groupCount ?: 0
        for (i in 0 until size) {
            expandGroup(i)
        }
    }

    /**
     * 收起全部
     */
    private fun collapseGroupAllGroups() {
        val size = adapter?.groupCount ?: 0
        for (i in 0 until size) {
            collapseGroup(i)
        }
    }

    fun scrollToGroup(groupPosition: Int) {
        if (groupPosition < (adapter?.groupCount ?: 0)) {
            setSelectedGroup(groupPosition)
        }
    }

    fun scrollToChild(groupPosition: Int, childPosition: Int, shouldExpandGroup: Boolean) {
        setSelectedChild(groupPosition, childPosition, shouldExpandGroup)
    }

    override fun addOnScrollListener(listener: OnScrollCompatListener) {
        if (scrollListeners == null) scrollListeners = mutableListOf()
        scrollListeners?.add(listener)
    }

    override fun ready() {
    }

    override fun stop(hasError: Boolean) {
    }

    override fun setListener(listener: LoadMore.Listener) {
    }

    private fun startLoadMore(state: LoadMore.State) {
        isLoadingMore = true
        loadMoreListener?.onLoading(state)
    }

    fun setFloatGroup(float: Boolean) {
        setFloatingGroupEnabled(float)
    }

    /**
     * 添加头部
     */
    fun addHeader(v: View) {
        addHeaderView(v)
    }

    /**
     * 添加尾部
     */
    fun addFooter(v: View) {
        addFooterView(v)
    }

}