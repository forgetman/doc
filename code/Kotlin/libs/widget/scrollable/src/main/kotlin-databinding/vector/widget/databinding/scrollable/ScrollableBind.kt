package vector.widget.databinding.scrollable

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import vector.widget.scrollable.OnChildClick
import vector.widget.scrollable.OnGroupClick

sealed class ScrollableBind {
    sealed class List : ScrollableBind() {
        data class OnScroll(val action: (recyclerView: RecyclerView?, scrollX: Int, scrollY: Int, state: Int) -> Unit) :
            List()

        data class OnScrollStateChanged(val action: (recyclerView: RecyclerView?, newState: Int) -> Unit) :
            List()

        data class OnItemClick(internal val action: (itemView: View, position: Int) -> Unit) : List()
        data class OnItemDoubleClick(internal val action: (itemView: View, position: Int) -> Unit) : List()
        data class OnItemLongClick(val action: (itemView: View, position: Int) -> Unit) : List()
    }

    sealed class GroupList : ScrollableBind() {
        data class OnGroupItemClick(val action: OnGroupClick) : GroupList()
        data class OnChildItemClick(val action: OnChildClick) : GroupList()
    }
}