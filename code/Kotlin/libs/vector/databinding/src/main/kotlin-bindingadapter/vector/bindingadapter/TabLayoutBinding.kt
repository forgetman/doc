@file:Suppress("unused")

package vector.bindingadapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.google.android.material.tabs.TabLayout
import vector.app.os.IntRes

/**
 * @author yuansui
 * @since 2019-07-24
 */
object TabLayoutBinding {
    private const val ATTRS = BINDING_PREFIX + "tabLayout_attrs"

    @JvmStatic
    @BindingAdapter(ATTRS)
    fun setAttrs(view: TabLayout, attrs: TabLayoutAttrs) {
        attrs.setupWithTabLayout(view)
    }
}

/**
 * 统一设置[TabLayout]的属性
 * @author yuansui
 * @since 2019-07-24
 */
class TabLayoutAttrs private constructor() {

    companion object {
        fun build(block: TabLayoutAttrs.() -> Unit): TabLayoutAttrs {
            val a = TabLayoutAttrs()
            block.invoke(a)
            return a
        }

    }

    enum class Mode(val id: Int) {
        SCROLLABLE(TabLayout.MODE_SCROLLABLE),
        FIXED(TabLayout.MODE_FIXED),
        AUTO(TabLayout.MODE_AUTO)
    }

    enum class Gravity(val id: Int) {
        FILL(TabLayout.GRAVITY_FILL),
        CENTER(TabLayout.GRAVITY_CENTER)
    }

    var gravity: Gravity = Gravity.FILL

    private var indicatorColor: Int? = null
    private var indicatorDrawable: Drawable? = null
    var indicatorHeight: Int? = null
    var indicatorFullWidth: Boolean? = null

    var tabRippleColor: ColorStateList? = null

    var mode: Mode = Mode.SCROLLABLE

    var textColorNormal: IntRes? = null
    var textColorSelected: IntRes? = null

    var listener: TabLayout.OnTabSelectedListener? = null

    fun setIndicatorColor(@ColorInt color: Int) {
        indicatorColor = color
    }

    fun setIndicatorDrawable(d: Drawable?, @ColorInt color: Int) {
        indicatorDrawable = d
        indicatorColor = color
    }

    fun setupWithTabLayout(layout: TabLayout) {
        layout.tabMode = mode.id
        layout.tabGravity = gravity.id

        tabRippleColor?.let {
            layout.tabRippleColor = it
        }

        indicatorFullWidth?.let {
            layout.isTabIndicatorFullWidth = it
        }

        indicatorDrawable?.let {
            layout.setSelectedTabIndicatorColor(indicatorColor ?: Color.TRANSPARENT)
            layout.setSelectedTabIndicator(indicatorDrawable)
        } ?: indicatorColor?.let { color ->
            layout.setSelectedTabIndicatorColor(color)
            @Suppress("DEPRECATION")
            indicatorHeight?.let { layout.setSelectedTabIndicatorHeight(it) }
        }

        val normal = textColorNormal?.getIntRelatedColor(layout.context) ?: 0
        val selected = textColorSelected?.getIntRelatedColor(layout.context) ?: 0
        layout.setTabTextColors(normal, selected)

        listener?.let {
            layout.addOnTabSelectedListener(it)
        }
    }
}