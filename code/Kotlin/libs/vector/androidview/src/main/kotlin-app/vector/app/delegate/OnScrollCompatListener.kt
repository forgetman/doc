package vector.app.delegate

import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView

/**
 * 共用的监听类
 * @see [vector.widget.scrollable.GroupListView]
 * @see [vector.widget.scrollable.ListView]
 *
 * @author yuansui
 */
abstract class OnScrollCompatListener : RecyclerView.OnScrollListener(), OnScrollListener {

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}

    override fun onScroll(
        view: AbsListView,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {}

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
}