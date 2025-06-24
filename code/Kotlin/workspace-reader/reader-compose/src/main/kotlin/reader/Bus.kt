package reader

import androidx.lifecycle.LifecycleOwner
import bus.api.Event
import bus.flow.FlowBus
import kotlinx.coroutines.CoroutineScope
import vector.singleton.Singleton2

object EventId {
    const val FINISH_ADD_BOOK = 0
    const val ADD_BOOK = 1 // 添加书本
    const val UPDATE_BOOK_READ_TIME = 2 // 更新阅读时间
    const val UPDATE_BOOK_CHAPTER_NUM = 3 // 更新书本章节数量
    const val UPDATE_BOOK_READ_INDEX = 4 // 更新书本阅读下标

    const val CACHE_DOWNLOAD_PROGRESS = 12
    const val CACHE_DOWNLOAD_FINISH = 13
    const val CHAPTER_DOWNLOAD_FINISH = 14

    const val TOUCH_AREA_LEFT = 20
    const val TOUCH_AREA_RIGHT = 21
    const val TOUCH_AREA_CENTER = 22

    const val RELOAD_BY_THEME_CHANGED = 23

    const val SWITCH_TO_BOOK_CITY = 50
}

/**
 * @author yuansui
 * @since 2018/8/11 0011
 */
class Bus private constructor() : FlowBus() {
    companion object : Singleton2<Bus> by Singleton2({
        Bus()
    })
}

fun sendMessage(eventId: Int) {
    Bus.getInstance().send(eventId)
}

fun <T> sendMessage(event: Event<T>) {
    Bus.getInstance().send(event)
}

fun sendMessage(id: Int, any: Any? = null) {
    Bus.getInstance().send(id, any)
}

val CoroutineScope.bus
    get() = Bus.getInstance().with(this)

val LifecycleOwner.bus
    get() = Bus.getInstance().with(this)

class DownloadEvent(id: Int) : Event<String>(id) {
    var bookId: String? = null
}

class BookUpdateChapterNum(id: Int) : Event<String>(id) {
    var bookId: String? = null
    var number: Int? = null
    var lastName: String? = null
}

class BookUpdateReadIndex(id: Int) : Event<String>(id) {
    var bookId: String? = null
    var index: Int? = null
}