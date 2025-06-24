package reader.pattern.frag

import android.graphics.Color
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.onEach
import reader.Bus
import reader.EventId
import reader.R
import reader.databinding.FragReadableBinding
import reader.ext.withViewState
import reader.model.Readable
import reader.pattern.repo.ReadableRepo
import reader.sendMessage
import reader.widget.ReadView
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.databinding.frag.SimpleDBFragEx
import vector.app.ext.bind.bindView
import javax.inject.Inject

/**
 * 每一章的内容
 * @author yuansui
 * @since 2018/9/8
 */
@Creator
@AndroidEntryPoint
@LayoutBindingClass<FragReadableBinding>
class ReadableFrag : SimpleDBFragEx() {

    @Extra
    lateinit var readable: Readable

    @Inject
    lateinit var repo: ReadableRepo

    private val readView by bindView<ReadView>(R.id.read_view)

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return FragReadableBinding.inflate(inflater)
    }

    override fun initializeContentView() {
        setBackgroundColor(Color.TRANSPARENT)

        if (readable.hasContent()) {
            readView.lines = readable.lines?.data
            readView.pageNumber = readable.pageNumber
        } else {
            val chapterId = readable.chapterId ?: return
            val hasCache = repo.hasCache(readable.bookId, chapterId)
            if (!hasCache) {
                fetchData(chapterId)
            } else {
                sendMessage(EventId.CHAPTER_DOWNLOAD_FINISH, chapterId)
                Bus.getInstance().send(EventId.CHAPTER_DOWNLOAD_FINISH, chapterId)
            }
        }

        readView.invalidate()

        Bus.getInstance().with(this).onMessage(EventId.RELOAD_BY_THEME_CHANGED) {
            readView.invalidate()
        }
    }

    private fun fetchData(chapterId: String) {
        repo.fetchText(readable.bookId, chapterId)
            .onEach {
                Bus.getInstance().send(EventId.CHAPTER_DOWNLOAD_FINISH, chapterId)
            }.withViewState(this).launchIn(this)
    }

    override fun onRetryClick() {
        fetchData(readable.chapterId ?: return)
    }

}
