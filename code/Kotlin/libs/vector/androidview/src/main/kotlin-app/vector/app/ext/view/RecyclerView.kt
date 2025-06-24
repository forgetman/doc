@file:Suppress("unused")

package vector.app.ext.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

fun RecyclerView.LayoutManager.findFirstVisibleItemPosition(): Int {
    return when (this) {
        is LinearLayoutManager -> findFirstVisibleItemPosition()
        is StaggeredGridLayoutManager -> {
            val array = findFirstVisibleItemPositions(null)
            if (array.isNotEmpty()) array[0] else RecyclerView.NO_POSITION
        }

        else -> RecyclerView.NO_POSITION
    }
}

fun RecyclerView.LayoutManager.findLastVisibleItemPosition(): Int {
    return when (this) {
        is LinearLayoutManager -> findLastVisibleItemPosition()
        is StaggeredGridLayoutManager -> {
            val array = findLastVisibleItemPositions(null)
            if (array.isNotEmpty()) array[0] else RecyclerView.NO_POSITION
        }

        else -> RecyclerView.NO_POSITION
    }
}