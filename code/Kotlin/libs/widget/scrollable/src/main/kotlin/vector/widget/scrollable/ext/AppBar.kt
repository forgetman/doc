@file:Suppress("unused")

package vector.widget.scrollable.ext

import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import vector.Constants
import vector.app.appbar.AppBar
import vector.app.delegate.OnScrollCompatListener
import vector.app.ext.view.doOnPreDraw
import vector.app.ext.view.findFirstVisibleItemPosition
import vector.widget.scrollable.delegate.ScrollDelegate

/**
 * 根据高度自动变换titleBar的背景色透明度
 *
 * @param inHeight 变换透明的总高度
 * @param delegate
 */
fun AppBar.fadeByScroll(
    delegate: ScrollDelegate,
    inHeight: Int,
    block: ((alpha: Int) -> Unit)? = null
) {
    if (height == 0 && visibility != View.GONE && viewTreeObserver.isAlive) {
        // 调用的时机不对. 获取不到appBar的高度
        doOnPreDraw {
            val barH = this.height
            if (barH != 0) {
                setAlpha(delegate, this, inHeight - barH, block)
            }
        }
    } else {
        setAlpha(delegate, this, inHeight - height, block)
    }
}

private fun setAlpha(
    delegate: ScrollDelegate,
    bar: AppBar,
    height: Int,
    block: ((alpha: Int) -> Unit)? = null
) {
    delegate.addOnScrollListener(object : OnScrollCompatListener() {
        override fun onScroll(
            view: AbsListView,
            firstVisibleItem: Int,
            visibleItemCount: Int,
            totalItemCount: Int
        ) {
            val alpha: Int
            when {
                firstVisibleItem == 0 -> {
                    val top = (-view.getChildAt(0).top).toFloat()
                    var rate = top / height
                    if (rate > 1) {
                        rate = 1f
                    }
                    alpha = (rate * Constants.ALPHA_MAX).toInt()
                }

                firstVisibleItem > 0 -> alpha = Constants.ALPHA_MAX.toInt()
                else -> alpha = 0
            }
            bar.backgroundAlpha = alpha
            block?.invoke(alpha)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = requireNotNull(recyclerView.layoutManager)
            val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            val alpha: Int
            when {
                firstVisibleItem == 0 -> {
                    val top = (-requireNotNull(recyclerView.getChildAt(0)?.top)).toFloat()
                    var rate = top / height
                    if (rate > 1) {
                        rate = 1f
                    }
                    alpha = (rate * Constants.ALPHA_MAX).toInt()
                }

                firstVisibleItem > 0 -> alpha = Constants.ALPHA_MAX.toInt()
                else -> alpha = 0
            }
            bar.backgroundAlpha = alpha
            block?.invoke(alpha)
        }
    })

}