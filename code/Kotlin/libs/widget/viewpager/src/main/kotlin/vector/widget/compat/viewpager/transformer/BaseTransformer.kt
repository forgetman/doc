package vector.widget.compat.viewpager.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager.PageTransformer as ViewPagerTransformer
import androidx.viewpager2.widget.ViewPager2.PageTransformer as ViewPager2Transformer

/**
 * PS: 所有都是对于v本身的处理, 所以特别注意translate的话应该是计算增量, 默认已经算完距离了
 *
 * @author yuansui
 */
abstract class BaseTransformer : ViewPagerTransformer, ViewPager2Transformer {

    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> onLeft(page, position)
            position <= 1 -> onTurn(page, position)
            else -> onRight(page, position)
        }
    }

    /**
     * This page is way off-screen to the left
     * [-Infinity,-1)
     */
    protected abstract fun onLeft(v: View, position: Float)

    /**
     * a页滑动至b页 ； a页[0, -1]；b页[1, 0]
     * [-1,1]
     */
    protected abstract fun onTurn(v: View, position: Float)

    /**
     * This page is way off-screen to the right
     * (1,+Infinity]
     */
    protected abstract fun onRight(v: View, position: Float)
}
