package vector.compat.media

import android.net.Uri
import java.io.File

class MediaData {
    var relativePath: String
    var displayName: String
    var absolutePath: String
    var uri: Uri

    constructor(relativePath: String, displayName: String, uri: Uri) {
        this.relativePath = relativePath
        this.displayName = displayName
        this.uri = uri
        absolutePath = relativePath + displayName
    }

    constructor(absolutePath: String, uri: Uri) {
        if (absolutePath.isEmpty()) throw IllegalArgumentException("absolutePath can not be empty")

        this.absolutePath = absolutePath
        this.uri = uri

        val index = absolutePath.lastIndexOf(File.separatorChar)
        if (index == -1) throw IllegalArgumentException("absolutePath is not a path")
        relativePath = absolutePath.substring(0, index + 1)
        displayName = absolutePath.substring(index + 1)
    }

    override fun toString(): String {
        return buildString {
            append("relativePath = $relativePath")
            append(" displayName = $displayName")
            append(" uri = $uri")
        }
    }
}