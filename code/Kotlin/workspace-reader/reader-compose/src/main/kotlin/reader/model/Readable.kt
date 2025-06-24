package reader.model

/**
 * @author yuansui
 * @since 2021/4/16
 */
//@TypeConverters(ReadableTypeConverter::class)
class Readable {
    var bookId: String? = null
    var chapterId: String? = null
    var title: String? = null
    var lines: ReadableLines? = null
    var pageNumber: String? = null

    fun hasContent() = lines != null
}

fun Chapter.toReadables(): List<Readable> {

    fun getEmptyReadable(): List<Readable> {
        return listOf(Readable().apply {
            bookId = this@toReadables.bookId
            chapterId = this@toReadables.id
            title = this@toReadables.name
        })
    }

    if (!prepared) {
        if (hasContent) {
            if (!preparePages()) return getEmptyReadable()
        } else {
            return getEmptyReadable()
        }
    }

    val readables = mutableListOf<Readable>()
    pages.forEachIndexed { index, readableLines ->
        readables.add(Readable().apply {
            bookId = this@toReadables.bookId
            chapterId = this@toReadables.id
            title = this@toReadables.name
            this.lines = readableLines
            pageNumber = this@toReadables.getPageNumber(index)
        })
    }

    return readables
}

data class ReadableLines(val data: List<String>?)

//class ReadableTypeConverter {
//
//    @TypeConverter
//    fun toJson(lines: ReadableLines): String {
//        return Eson.default().toJson(lines)
//    }
//
//    @TypeConverter
//    fun fromJson(json: String): ReadableLines {
//        val type = object : TypeToken<ReadableLines>() {}.type
//        Eson.default().fromJson(json, type)
//        return Eson.default().fromJson(json, type) ?: null
//    }
//}