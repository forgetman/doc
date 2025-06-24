package vector.ext

import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import logger.L
import vector.appContext

fun toast(
    vararg texts: String?,
    context: Context = appContext,
    duration: Int = Toast.LENGTH_SHORT
) {
    if (texts.isEmpty()) return

    var text = String()
    texts.forEach {
        text = text.plus(it)
    }

    if (text.isNotEmpty() && text.isNotBlank()) {
        Toast.makeText(context, text, duration).show()
    }
}

fun toast(
    @StringRes vararg ids: Int,
    context: Context = appContext,
    duration: Int = Toast.LENGTH_SHORT
) {
    if (ids.isEmpty()) return

    var text = String()
    try {
        ids.forEach {
            text = text.plus(ContextCompat.getString(context, it))
        }
    } catch (e: Resources.NotFoundException) {
        L.e(e)
        return
    }

    if (text.isNotEmpty() && text.isNotBlank()) {
        Toast.makeText(context, text, duration).show()
    }
}