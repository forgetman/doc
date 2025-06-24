package reader.pattern.adapter

import androidx.databinding.ViewDataBinding
import reader.BR
import reader.databinding.LayoutChapterItemBinding
import reader.databinding.LayoutFontDisplayBinding
import reader.databinding.LayoutLeaderboardItemBinding
import reader.databinding.LayoutSearchHotItemBinding
import reader.databinding.LayoutSearchResultItemBinding
import reader.model.Book
import reader.model.Chapter
import reader.model.FontDisplay
import vector.widget.databinding.scrollable.adapter.DBItemBinder

/**
 * @author yuansui
 * @since 2018/4/10
 */
abstract class BaseDBItemBinder<T, VDB : ViewDataBinding> : DBItemBinder<T, VDB>() {

    override fun onBindBinding(item: T, binding: VDB, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.owner, this)
        binding.executePendingBindings()
    }
}

class ChaptersItemItemBinder : BaseDBItemBinder<Chapter, LayoutChapterItemBinding>()

class SearchItemItemBinder : BaseDBItemBinder<Book, LayoutSearchResultItemBinding>()

class SearchHotItemItemBinder : BaseDBItemBinder<String, LayoutSearchHotItemBinding>()

class LeaderboardItemItemBinder : BaseDBItemBinder<Book, LayoutLeaderboardItemBinding>()

class FontDisplayItemItemBinder : BaseDBItemBinder<FontDisplay, LayoutFontDisplayBinding>()