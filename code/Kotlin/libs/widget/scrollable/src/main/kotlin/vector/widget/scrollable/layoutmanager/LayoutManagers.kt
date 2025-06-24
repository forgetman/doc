package vector.widget.scrollable.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import logger.L

/**
 * A collection of factories to create RecyclerView LayoutManagers so that you can easily set them
 * in your layout.
 * @author yuansui
 * @since 2018/3/7
 */
@Suppress("unused")
object LayoutManagers {

    interface LayoutManagerFactory {
        fun create(context: Context?): RecyclerView.LayoutManager
    }

    fun linear(
        orientation: Orientation = Orientation.VERTICAL,
        reverseLayout: Boolean = false,
        stackFromEnd: Boolean = false
    ): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(context: Context?): RecyclerView.LayoutManager {
                return FixLinearLayoutManager(context, orientation.int, reverseLayout).apply {
                    this.stackFromEnd = stackFromEnd
                }
            }
        }
    }

    fun grid(
        spanCount: Int,
        orientation: Orientation = Orientation.VERTICAL,
        reverseLayout: Boolean = false,
        stackFromEnd: Boolean = false
    ): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(context: Context?): RecyclerView.LayoutManager {
                return FixGridLayoutManager(
                    context,
                    spanCount,
                    orientation.int,
                    reverseLayout
                ).apply {
                    this.stackFromEnd = stackFromEnd
                }
            }
        }
    }

    fun staggeredGrid(
        spanCount: Int,
        orientation: Orientation = Orientation.VERTICAL,
        reverseLayout: Boolean = false
    ): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(context: Context?): RecyclerView.LayoutManager {
                val manager = StaggeredGridLayoutManager(spanCount, orientation.int)
                manager.reverseLayout = reverseLayout
                return manager
            }
        }
    }

    fun flexbox(): LayoutManagerFactory {
        return object : LayoutManagerFactory {
            override fun create(context: Context?): RecyclerView.LayoutManager {
                return FlexboxLayoutManager(context)
            }
        }
    }

    enum class Orientation(val int: Int) {
        VERTICAL(LinearLayoutManager.VERTICAL),
        HORIZONTAL(LinearLayoutManager.HORIZONTAL),
    }
}

internal open class FixGridLayoutManager(
    context: Context?,
    spanCount: Int,
    orientation: Int,
    reverseLayout: Boolean
) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        /**
         * fix bug:
         * java.lang.IndexOutOfBoundsException Inconsistency detected. Invalid item position xxx
         */
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            L.e(e)
        }
    }
}

internal class FixLinearLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        /**
         * fix bug:
         * java.lang.IndexOutOfBoundsException Inconsistency detected. Invalid item position xxx
         */
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            L.e(e)
        }
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        try {
            return super.scrollVerticallyBy(dy, recycler, state)
        } catch (e: Exception) {
            L.e(e)
        }

        return 0
    }
}