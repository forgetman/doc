package vector.widget.scrollable.decoration

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * @author yuansui
 * @since 2020/1/16
 */
abstract class BaseAttrs {
    /**
     * 分割线颜色, 默认灰色
     */
    @ColorInt
    var color: Int = Color.GRAY

    /**
     * 分割线尺寸(横向指高度, 纵向指宽度)
     */
    var size: Int? = null

    /**
     * 头部偏移量
     */
    var headerOffset: Int = 0

    /**
     * 脚部偏移量
     */
    var footerOffset: Int = 0
}

class LinearAttrs : BaseAttrs() {
    /**
     * [LinearLayoutManager.HORIZONTAL] 整个水平线性布局的左边
     */
    var drawStart: Boolean = true

    /**
     * [LinearLayoutManager.VERTICAL] 整个垂直线性布局的顶部
     */
    var drawTop: Boolean = true

    /**
     * [LinearLayoutManager.HORIZONTAL] 整个水平线性布局的右边
     */
    var drawEnd: Boolean = true

    /**
     * [LinearLayoutManager.VERTICAL] 整个垂直线性布局的底部
     */
    var drawBottom: Boolean = true

    /**
     * 分割线左边距
     */
    var marginStart: Int = 0

    /**
     * 分割线上边距
     */
    var marginTop: Int = 0

    /**
     * 分割线右边距
     */
    var marginEnd: Int = 0

    /**
     * 分割线下边距
     */
    var marginBottom: Int = 0
}

class GridAttrs : BaseAttrs() {
    /**
     *  整个垂直线性布局的顶部
     */
    var drawTop: Boolean = true

    /**
     * 整个网络布局的底部
     */
    var drawBottom: Boolean = true

    /**
     * 分割线上边距
     */
    var marginTop: Int = 0

    /**
     * 分割线下边距
     */
    var marginBottom: Int = 0
}