@file:Suppress("unused")

package vector.ext

import java.io.InputStream
import java.io.ObjectOutputStream
import java.io.OutputStream

fun InputStream.readString(): String {
    return String(readBytes()).replaceLineBreak()
}

fun OutputStream.obj(): ObjectOutputStream {
    return if (this is ObjectOutputStream) this else ObjectOutputStream(this)
}
