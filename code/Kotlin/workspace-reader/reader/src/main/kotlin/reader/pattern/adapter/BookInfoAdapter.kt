package reader.pattern.adapter

import reader.databinding.LayoutBookInfoHeaderBinding
import reader.databinding.LayoutBookInfoMoreItemBinding
import reader.model.Book
import vector.widget.databinding.scrollable.adapter.DBItemBinder

interface BookInfoAdapter {
    interface Data
    data class Header(val book: Book) : Data
    data class More(val book: Book) : Data

    interface Binder {
        class Header : DBItemBinder<BookInfoAdapter.Header, LayoutBookInfoHeaderBinding>() {
            override fun onBindBinding(
                item: BookInfoAdapter.Header,
                binding: LayoutBookInfoHeaderBinding,
                position: Int
            ) {
                binding.item = item.book
            }

            override fun getSpanSize(position: Int, spanCount: Int): Int {
                return spanCount
            }
        }

        class More : DBItemBinder<BookInfoAdapter.More, LayoutBookInfoMoreItemBinding>() {
            override fun onBindBinding(
                item: BookInfoAdapter.More,
                binding: LayoutBookInfoMoreItemBinding,
                position: Int
            ) {
                binding.item = item.book
            }
        }
    }
}