package vector.widget.databinding.scrollable.binding

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BindingAdapter
import vector.bindingadapter.BINDING_PREFIX
import vector.os.Group
import vector.widget.databinding.scrollable.ScrollableBind
import vector.widget.scrollable.GroupListView
import vector.widget.scrollable.adapter.GroupMultiAdapterEx

/**
 * @author yuansui
 * @since 2019/1/22
 */
object GroupListViewBinding {

    private const val ADAPTER = BINDING_PREFIX + "groupListView_adapter"
    private const val DATA = BINDING_PREFIX + "groupListView_data"

    private const val HEADER = BINDING_PREFIX + "grouplistView_adapterHeader"
    private const val FOOTER = BINDING_PREFIX + "groupListView_footer"

    private const val ON_GROUP_CLICK = BINDING_PREFIX + "groupListView_onGroupClick"
    private const val ON_CHILD_CLICK = BINDING_PREFIX + "groupListView_onChildClick"

//    private const val ON_LOAD_MORE = BINDING_PREFIX + "groupListView_onLoadMore"

    private const val EXPAND_ALL_GROUPS = BINDING_PREFIX + "groupListView_expandAllGroups"

    private const val SCROLL_TO_GROUP = BINDING_PREFIX + "groupListView_scrollToGroup"
    private const val DIVIDER = BINDING_PREFIX + "groupListView_divider"
    private const val GROUP_INDICATOR = BINDING_PREFIX + "groupListView_indicator"

    private const val EXPAND = BINDING_PREFIX + "groupListView_expand"
    private const val COLLAPSE = BINDING_PREFIX + "groupListView_collapse"

    private const val FLOAT_GROUP = BINDING_PREFIX + "groupListView_floatGroup"

    @JvmStatic
    @BindingAdapter(SCROLL_TO_GROUP)
    fun setScrollToGroup(view: GroupListView, groupPosition: Int) {
        view.scrollToGroup(groupPosition)
    }

    @JvmStatic
    @BindingAdapter(EXPAND_ALL_GROUPS)
    fun setExpandAllGroups(view: GroupListView, expandAll: Boolean) {
        view.expandAll = expandAll
    }

    @JvmStatic
    @BindingAdapter(ADAPTER, DATA, HEADER, FOOTER, requireAll = false)
    fun <GROUP : Group<CHILD>, CHILD, VH, A : GroupMultiAdapterEx<GROUP, CHILD, VH>>
        setAdapter(
        view: GroupListView,
        adapter: A,
        data: List<GROUP>?,
        header: View?,
        footer: View?
    ) {
        if (view.adapter == null || view.adapter != adapter) {
            view.adapter = adapter

            header?.let { view.addHeader(it) }
            footer?.let { view.addFooter(it) }
        }

        adapter.data = data
    }

    @JvmStatic
    @BindingAdapter(ON_GROUP_CLICK)
    fun setOnGroupClick(view: GroupListView, binding: ScrollableBind.GroupList.OnGroupItemClick) {
        view.setOnGroupClickListener(binding.action)
    }

    @JvmStatic
    @BindingAdapter(ON_CHILD_CLICK)
    fun setOnChildClick(view: GroupListView, binding: ScrollableBind.GroupList.OnChildItemClick) {
        view.setOnChildClickListener(binding.action)
    }

    @JvmStatic
    @BindingAdapter(DIVIDER)
    fun setDivider(view: GroupListView, divider: Drawable?) {
        view.divider = divider
    }

    @JvmStatic
    @BindingAdapter(GROUP_INDICATOR)
    fun setGroupIndicator(view: GroupListView, indicator: Drawable?) {
        view.setGroupIndicator(indicator)
    }

    @JvmStatic
    @BindingAdapter(EXPAND)
    fun setExpand(view: GroupListView, groupPosition: Int?) {
        groupPosition?.let {
            view.expandGroup(it)
        }
    }

    @JvmStatic
    @BindingAdapter(COLLAPSE)
    fun setCollapse(view: GroupListView, groupPosition: Int?) {
        groupPosition?.let {
            view.collapseGroup(it)
        }
    }

    @JvmStatic
    @BindingAdapter(FLOAT_GROUP)
    fun setFloatGroup(groupListView: GroupListView, float: Boolean) {
        groupListView.setFloatGroup(float)
    }

}