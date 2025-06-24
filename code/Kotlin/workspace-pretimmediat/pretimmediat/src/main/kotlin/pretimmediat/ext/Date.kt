package pretimmediat.ext

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_FORMAT_PATTERN_YMD = "dd-MM-yyyy"

fun String.formatDate(): Long {
    return runCatching {
        SimpleDateFormat(DATE_FORMAT_PATTERN_YMD, Locale.FRANCE).parse(this)?.time ?: 0L
    }.getOrDefault(0L)
}

fun Long.formatDate(): String {
    return SimpleDateFormat(DATE_FORMAT_PATTERN_YMD, Locale.FRANCE).format(Date(this));
}