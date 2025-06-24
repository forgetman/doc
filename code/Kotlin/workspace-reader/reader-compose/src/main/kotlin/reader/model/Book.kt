package reader.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import live.Live
import vector.EMPTY
import vector.app.configuration.Configurations.ui
import androidx.room.Ignore as RoomIgnore
import eson.Ignore as ParseIgnore

/**
 * @author yuansui
 * @since 2017/12/11
 */
@Entity(tableName = "table_book")
class Book {

    @PrimaryKey
    @SerializedName("Id")
    var id: String = EMPTY

    @SerializedName("LastChapterId")
    var lastChapterId: String = EMPTY

    @SerializedName("Name")
    var name: String = EMPTY

    @SerializedName("Desc")
    var intro: String = EMPTY

    @SerializedName("Author")
    var author: String = EMPTY

    @SerializedName("Img")
    var img: String = EMPTY // 图片
        get() {
            return if (field.startsWith("http")) {
                field
            } else {
                "http://imgapi.jiaston.com/BookFiles/BookImages/$field"
            }
        }

    @SerializedName("Score")
    var score: String? = null // 评分

    var index: Int = 0 // 当前阅读的章节
        set(value) {
            field = value
//            setCurrReadInfos(value, chapterNum)
        }

    @SerializedName("LastChapter")
    var newChapterDesc: String = EMPTY // 最新章节描述
        set(value) {
            field = value
            lastChapterDesc.value = value
        }

    // 最新章节展示
    @RoomIgnore
    @ParseIgnore
    val lastChapterDesc = Live(EMPTY)

    @SerializedName("CName")
    var type: String? = null // 小说类型

    @SerializedName("BookStatus")
    var status: String? = null // 连载状态
        set(value) {
            field = value
//            ui.statusVisible.postValue(value == "完结")
        }

    var chapterNum: Int = -1 // 章节数量
        set(value) {
            field = value
//            setCurrReadInfos(index, value)
        }

    @SerializedName("LastTime")
    var lastUpdateTime: String? = null

    @ParseIgnore
    var readTime: Long = 0

    @RoomIgnore
    @SerializedName("SameUserBooks")
    var sameUserBooks: MutableList<Book>? = null

    @Deprecated(
        message = "暂时保留字段, 数据库不支持删除列",
        replaceWith = ReplaceWith("this.readTime"),
        level = DeprecationLevel.WARNING
    )
    @ParseIgnore
    var updateTime: Long = 0

    @Deprecated(
        message = "已无用, 暂时保留字段, 数据库不支持删除列",
        level = DeprecationLevel.WARNING
    )
    @ParseIgnore
    var page: Int = 0 // 当前章节的页码

//    private fun setCurrReadInfos(index: Int, max: Int) {
//        if (max <= 0) {
//            ui.currReadIndex.value = "待更新"
//            ui.currReadProgress.value = "0%"
//        } else {
//            ui.currReadIndex.value = buildString {
//                append(index + 1)
//                append("/")
//                append(max)
//                append("章")
////                append(Res.getString(R.string.chapter_short))
//            }
//            val progress: Float = (index + 1) / max.toFloat()
//            ui.currReadProgress.value = "${progress.times(100).toInt()}%"
//        }
//    }

    override fun equals(other: Any?): Boolean {
        if (other !is Book) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + lastChapterId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + intro.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + (score?.hashCode() ?: 0)
        result = 31 * result + index
        result = 31 * result + newChapterDesc.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + chapterNum
        result = 31 * result + (lastUpdateTime?.hashCode() ?: 0)
        result = 31 * result + ui.hashCode()
        return result
    }
}
