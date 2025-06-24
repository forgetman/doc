@file:Suppress("unused")

package vector

const val EMPTY = ""

enum class UriMode(val value: String) {
    READ("r"), // read-only access
    WRITE("w"), // write-only access (erasing whatever data is currently in the file)
    APPEND("wr"), // write-only access to append to any existing data
    READ_WRITE("rw"), // read and write access on any existing data
    READ_WRITE_TRUNCATES("rwt") // read and write access that truncates any existing file
}

enum class FileMode(val value: String) {
    READ("r"),
    WRITE("w"),
    APPEND("r")
}

/**
 * 统一管理文件存储类型
 * suffix: 用于file系统
 * media: 用于MediaStore系统
 */
sealed class MimeType(val suffix: String, val media: String) {

    /**
     * 不指定类型, 即filename没有suffix
     */
    object Unspecified : MimeType(EMPTY, EMPTY)

    sealed class Image(suffix: String, value: String) : MimeType(suffix, value) {
        object Png : Image(".png", "image/png")
        object Jpeg : Image(".jpeg", "image/jpeg")
        object Jpg : Image(".jpg", "image/jpg")
        object Gif : Image(".gif", "image/gif")
    }

    sealed class Text(suffix: String, value: String) : MimeType(suffix, value) {
        object Txt : Text(".txt", "text/plain")
        object Xml : Text(".xml", "text/xml")
        object Csv : Text(".csv", "text/csv")
    }

    sealed class Audio(suffix: String, value: String) : MimeType(suffix, value) {
        object Amr : Audio(".amr", "audio/amr")
        object Mp3 : Audio(".mp3", "audio/mp3")
        object Mp4 : Audio(".mp4", "audio/mp4")
        object Wav : Audio(".wav", "audio/wav")
        object Aac : Audio(".aac", "audio/aac")
        object Ogg : Audio(".ogg", "audio/ogg")
    }

    sealed class Application(suffix: String, value: String) : MimeType(suffix, value) {
        object Doc : Application(".doc", "application/msword")
        object Docx : Application(".docx", "application/msword")
        object Ppt : Application(".ppt", "application/vnd.ms-powerpoint")
        object Pptx : Application(".pptx", "application/vnd.ms-powerpoint")
        object Js : Application(".js", "application/x-javascript")
        object Pdf : Application(".pdf", "application/pdf")
    }

    sealed class Other(suffix: String, value: String) : MimeType(suffix, value) {
        object Apk : Other(".apk", "???"/*type未知*/)
    }
}