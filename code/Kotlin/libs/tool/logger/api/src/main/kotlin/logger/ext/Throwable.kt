package logger.ext

import java.io.PrintWriter
import java.io.StringWriter
import java.net.UnknownHostException

fun Throwable.getStackTraceString(): String {
    var t: Throwable? = this
    while (t != null) {
        if (this is UnknownHostException) {
            return ""
        }
        t = t.cause
    }
    return StringWriter().let {
        val pw = PrintWriter(it)
        printStackTrace(PrintWriter(pw))
        pw.flush()
        it.toString()
    }
}
