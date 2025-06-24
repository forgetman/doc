package vector.widget.scrollable.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vector.widget.scrollable.adapter.binder.EmptyItemBinder
import vector.widget.scrollable.delegate.SpanSizeDelegate

/**
 * @author yuansui
 * @since 2021/4/30
 */
class EmptyAdapter : RecyclerView.Adapter<ItemViewHolder>(), SpanSizeDelegate {

    lateinit var binder: EmptyItemBinder<*>

    private var count: Int = 0

    internal var isEmpty: Boolean = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            if (field == value) {
                return
            }
            field = value
            when (value) {
                false -> {
                    count = 0
                    notifyDataSetChanged()
                }

                true -> {
                    count = 1
                    notifyDataSetChanged()
                }
            }
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return binder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        binder.onBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int {
        // 只是初始化的时候会获取一次, 返回0表示先不展示, 等待set [isEmpty]处理
        return count
    }

    override fun getSpanSize(position: Int, spanCount: Int): Int {
        return binder.getSpanSize(position, spanCount)
    }
}