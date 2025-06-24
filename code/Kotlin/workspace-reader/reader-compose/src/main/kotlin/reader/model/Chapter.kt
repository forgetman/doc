package reader.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.reflect.TypeToken
import eson.Eson
import live.Live
import logger.L
import reader.R
import reader.util.PageDrawer
import vector.EMPTY
import androidx.room.Ignore as RoomIgnore
import eson.Ignore as ParseIgnore

/**
 * 章节目录
 *
 * @author yuansui
 * @since 2017/6/27
 */
class Chapter {

    companion object {
        private const val LOG_TAG = "Chapter"

        private const val PAGE_NUMBER_SPLIT = '/'
        private const val VALID_KEYWORD_ENTER = "\n"
        private const val WHITE_SPACE = "　　" // 中文段落开头空格

        private val replaceable = mapOf(
            "&nsp;" to EMPTY,
            "&nbsp;" to EMPTY,
            "&&&&" to EMPTY,
            "&amp;" to EMPTY,
            "&lt;" to EMPTY,
            "&gt" to EMPTY,
            "/br" to EMPTY,
            "</p>" to EMPTY,
            "\r" to EMPTY,
            " " to EMPTY, // 英文空格
            "　" to EMPTY, // 中文空格
            "<br/>" to VALID_KEYWORD_ENTER,
            "nbsp;nbsp;" to VALID_KEYWORD_ENTER,
            "\n\n\n" to VALID_KEYWORD_ENTER,
            "\n\n" to VALID_KEYWORD_ENTER,
            "\r\n\r\n" to VALID_KEYWORD_ENTER,
        )
    }

    var id: String = EMPTY
    var name: String = EMPTY // 标题
    var bookId: String? = null

    @RoomIgnore
    @ParseIgnore
    val focus = Live<Boolean>()  // 是否为当前章节

    @RoomIgnore
    @ParseIgnore
    var text: String? = null // 章节原内容
        set(value) {
            if (value == null) return
            field = fixContent(removeInvalidContent(value))
            L.d(LOG_TAG, "content = $field")
        }

    @RoomIgnore
    @ParseIgnore
    val pages = mutableListOf<ReadableLines>() // 拆解的内容

    @RoomIgnore
    @ParseIgnore
    var pageIndex: Int = 0 // 当前页码下标

    val pageSize: Int
        get() = pages.size // 总页数

    val hasContent: Boolean
        get() = !text.isNullOrEmpty()

    @RoomIgnore
    @ParseIgnore
    var prepared: Boolean = false

    fun getPageNumber(index: Int? = null): String {
        return if (prepared) {
            buildString {
                append("第")
//                append(Res.getString(R.string.sort))
                append((index ?: pageIndex) + 1)
                append(PAGE_NUMBER_SPLIT)
                append(pageSize)
                append("页")
//                append(Res.getString(R.string.page))
            }
        } else EMPTY
    }

    fun preparePages(): Boolean {
        val list = PageDrawer.prepare(text) ?: return false
        pages.clear()
        pages.addAll(list)

        prepared = true
        return true
    }

    private fun removeInvalidContent(content: String): String {
        var newContent = content
        replaceable.forEach { (old, new) ->
            newContent = newContent.replace(old, new, true)
        }
        return newContent
    }

    /**
     * 检测段落之间是否有多个连续的\n存在, 合并为一个\n, 并自动添加段落开头的空格
     */
    private fun fixContent(content: String): String {
        return buildString {
            content.split(VALID_KEYWORD_ENTER)
                .filterNot {
                    it.isEmpty()
                }.forEach { s ->
                    append(WHITE_SPACE)
                    append(s)
                    append(VALID_KEYWORD_ENTER)
                }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Chapter) return false
        return this.id == other.id && this.name == other.name
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}

@TypeConverters(ChapterTypeConverter::class)
@Entity(tableName = "table_chapters")
class Chapters {

    constructor()

    @RoomIgnore
    constructor(id: String?, list: List<Chapter>?) {
        this.id = id ?: EMPTY
        this.list = list
    }

    @PrimaryKey
    var id: String = EMPTY // bookId

    var list: List<Chapter>? = null
}

class ChapterTypeConverter {

    @TypeConverter
    fun toJson(list: List<Chapter>): String {
        return Eson.default().toJson(list)
    }

    @TypeConverter
    fun fromJson(json: String): List<Chapter> {
        val type = object : TypeToken<MutableList<Chapter>>() {}.type
        return Eson.default().fromJson(json, type) ?: mutableListOf()
    }
}