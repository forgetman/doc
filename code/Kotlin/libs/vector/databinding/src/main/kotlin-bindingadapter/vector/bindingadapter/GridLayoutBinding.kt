@file:Suppress("unused")

package vector.bindingadapter

import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.gridlayout.widget.GridLayout
import vector.app.ext.inflate
import vector.util.LayoutParamsFactory

private typealias ViewAction = (View) -> Unit

class GridLayoutSet {

    class LayoutType private constructor(val type: Type, val width: Int) {

        enum class Type {
            AVERAGE, EXACT
        }

        companion object {
            fun average(width: Int) = LayoutType(Type.AVERAGE, width)
            fun exact(width: Int) = LayoutType(Type.EXACT, width)
        }
    }

    @LayoutRes
    var id = 0

    var layout: View? = null

    var layoutType: LayoutType? = null

    // 创建完view以后的回调
    var onDataSet: ViewAction? = null

    var onClick: ViewAction? = null

    // 是否计算行/列间距
    var calculateSpec: Boolean = true

    var bottomMargin = 0
}

/**
 * @author CaiXiang
 * @since 2018/12/27
 */
object GridLayoutBinding {

    private const val DATA = BINDING_PREFIX + "gridLayout_data"
    private const val COLUMN_COUNT = BINDING_PREFIX + "gridLayout_columnCount"

    @JvmStatic
    @BindingAdapter(DATA, COLUMN_COUNT, requireAll = false)
    fun setData(view: GridLayout, data: List<GridLayoutSet>?, columnCount: Int = 1) {
        view.removeAllViews()

        view.columnCount = columnCount

        data?.forEach { set ->
            val v = set.layout ?: view.context.inflate(set.id)

            set.onDataSet?.invoke(v)
            set.onClick?.let { action ->
                v.setOnClickListener(action)
            }

            if (set.calculateSpec) {
                set.layoutType?.let { type ->
                    when (type.type) {
                        GridLayoutSet.LayoutType.Type.AVERAGE -> {
                            val params = LayoutParamsFactory.grid(w = type.width / columnCount)
                            view.addView(v, params)
                        }

                        GridLayoutSet.LayoutType.Type.EXACT -> {
                            val params = LayoutParamsFactory.grid(w = type.width).apply {
                                bottomMargin = set.bottomMargin
                            }
                            view.addView(v, params)
                        }
                    }
                } ?: view.addView(v)
            } else {
                view.addView(v)
            }
        }

    }

}