package reader.pattern.frag.main

import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import coroutine.flow.launchIn
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import reader.BookUpdateChapterNum
import reader.BookUpdateReadIndex
import reader.EventId
import reader.R
import reader.bus
import reader.databinding.FragBookshelfBinding
import reader.datastore.Settings
import reader.model.Book
import reader.pattern.activity.BookInfoActivity
import reader.pattern.activity.BookInfoActivityCreator
import reader.pattern.activity.ReadActivityCreator
import reader.pattern.adapter.BookshelfAdapter
import reader.pattern.dialog.DeleteDialog
import reader.pattern.dialog.OptionDialog
import reader.pattern.popup.MenuPopup
import reader.pattern.viewModel.BookshelfViewModel
import reader.sendMessage
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.databinding.frag.DBFragEx
import vector.app.os.dp
import vector.app.os.drawableRes
import vector.app.popup.DimMode
import vector.datastore.preference.putEnum
import vector.ext.toast

enum class ShelfLayoutStyle {
    GRID,
    LINEAR
}

/**
 * 书架
 * @author yuansui
 * @since 2018/5/31
 */
@LayoutBindingClass<FragBookshelfBinding>
class BookshelfFrag : DBFragEx<BookshelfViewModel>() {

    companion object {
        const val GRID_SPAN_COUNT = 3
    }

    val binders = listOf(
        BookshelfAdapter.Binder.Header(),
        BookshelfAdapter.Binder.Grid(),
        BookshelfAdapter.Binder.Linear()
    )
    val emptyBinder =
        BookshelfAdapter.Binder.Empty(object : BookshelfAdapter.Binder.Empty.Listener {
            override fun onEmptyClick() {
                sendMessage(EventId.SWITCH_TO_BOOK_CITY)
            }
        })

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragBookshelfBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText {
            text = "书架"
            bold = true
        }

        appBar.left.addIcon(R.drawable.nav_bar_ic_search.drawableRes) {
            toast("敬请期待")
        }

        appBar.right.addIcon(R.drawable.nav_bar_ic_more.drawableRes) {
            val popup = MenuPopup(context)
            popup.onUpdateClick = {
                viewModel.updateAllBooks()
                popup.dismiss()
            }

            popup.onSwitchToGridClick = {
                lifecycleScope.launch {
                    Settings.shelfStyle.putEnum(ShelfLayoutStyle.GRID)
                }

                popup.dismiss()
            }

            popup.onSwitchToListClick = {
                lifecycleScope.launch {
                    Settings.shelfStyle.putEnum(ShelfLayoutStyle.LINEAR)
                }

                popup.dismiss()
            }

            popup.dimMode = DimMode.Normal(0.2f)
            popup.showAsDropDown(it, -14.dp.toPx(this), 10.dp.toPx(this), Gravity.END)
        }

        appBar.right.addIcon(R.drawable.nav_bar_ic_theme.drawableRes) {
            // TODO: 暂时关闭弹窗, 等待夜间模式的皮肤设计完成
//            val popup = ThemePopup(context)
//            popup.onDayClick = {
//                popup.dismiss()
//                sendMessage(EventId.SWITCH_THEME, DayNightMode.DAY)
//            }
//
//            popup.onNightClick = {
//                popup.dismiss()
//                sendMessage(EventId.SWITCH_THEME, DayNightMode.NIGHT)
//            }
//
//            popup.onFollowClick = {
//                popup.dismiss()
//                sendMessage(EventId.SWITCH_THEME, DayNightMode.FOLLOW_SYSTEM)
//            }
//
//            popup.dimMode = DimMode.Normal(0.2f)
//            popup.showAsDropDown(it, -14.dp.toPx(this), 10.dp.toPx(this), Gravity.END)
            toast("敬请期待")
        }
    }

    override fun initializeContentView() {
        with(viewModel) {
            getAll()

            onBookSelected.filterNotNull().onEach {
                ReadActivityCreator.create(it).start(context)
            }.launchIn(viewLifecycleOwner)

            option.observe(viewLifecycleOwner) {
                val dialog = OptionDialog(context)

                dialog.onDeleteSelected = {
                    val d = DeleteDialog(context)
                    d.onDeleteConfirm = {
                        viewModel.delete(it)
                    }
                    d.show()
                }

                dialog.onUpdateSelected = {
                    viewModel.updateBook(it.id)
                }

                dialog.onInfoSelected = {
                    BookInfoActivityCreator.create(it.id, BookInfoActivity.From.CHECK)
                        .start(context)
                }

                dialog.show()
            }
        }

        bus.onValue<Book>(EventId.UPDATE_BOOK_READ_TIME) { book ->
            viewModel.updateReadTime(book)
        }

        bus.onValue<Book>(EventId.ADD_BOOK) { book ->
            viewModel.addNewBook(book)
        }

        bus.onEvent<BookUpdateChapterNum>(EventId.UPDATE_BOOK_CHAPTER_NUM) {
            viewModel.updateBookChapter(it.bookId, it.number, it.lastName)
        }

        bus.onEvent<BookUpdateReadIndex>(EventId.UPDATE_BOOK_READ_INDEX) {
            viewModel.updateBookReadIndex(it.bookId, it.index)
        }
    }

}