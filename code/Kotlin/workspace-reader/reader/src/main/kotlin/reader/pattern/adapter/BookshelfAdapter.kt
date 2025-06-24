package reader.pattern.adapter

import live.Live
import reader.databinding.LayoutBookshelfItemEmptyBinding
import reader.databinding.LayoutBookshelfItemStyleGridBinding
import reader.databinding.LayoutBookshelfItemStyleHeadBinding
import reader.databinding.LayoutBookshelfItemStyleLinearBinding
import reader.model.Book
import vector.app.os.dp
import vector.widget.databinding.scrollable.adapter.DBItemBinder
import vector.widget.databinding.scrollable.adapter.EmptyDBItemBinder

interface BookshelfAdapter {
    interface Data
    data class Header(val book: Book) : Data
    data class Grid(val book: Book) : Data
    data class Linear(val book: Book) : Data {
        val marginTop = Live<Int>()
    }

    interface Binder {
        class Header :
            DBItemBinder<BookshelfAdapter.Header, LayoutBookshelfItemStyleHeadBinding>() {

            override fun onBindBinding(
                item: BookshelfAdapter.Header,
                binding: LayoutBookshelfItemStyleHeadBinding,
                position: Int
            ) {
                binding.item = item.book
            }

            override fun getSpanSize(position: Int, spanCount: Int): Int {
                return spanCount
            }
        }

        class Grid : DBItemBinder<BookshelfAdapter.Grid, LayoutBookshelfItemStyleGridBinding>() {
            override fun onBindBinding(
                item: BookshelfAdapter.Grid,
                binding: LayoutBookshelfItemStyleGridBinding,
                position: Int
            ) {
                binding.item = item.book
            }
        }

        class Linear :
            DBItemBinder<BookshelfAdapter.Linear, LayoutBookshelfItemStyleLinearBinding>() {
            override fun onBindBinding(
                item: BookshelfAdapter.Linear,
                binding: LayoutBookshelfItemStyleLinearBinding,
                position: Int
            ) {
                if (position == 1) {
                    item.marginTop.value = 0
                } else {
                    item.marginTop.value = 14.dp.toPx(binding.root.context)
                }
                binding.item = item
            }
        }

        class Empty(val listener: Listener) : EmptyDBItemBinder<LayoutBookshelfItemEmptyBinding>() {

            interface Listener {
                fun onEmptyClick()
            }

            override fun onBindBinding(binding: LayoutBookshelfItemEmptyBinding) {
                binding.listener = listener
            }
        }
    }
}
