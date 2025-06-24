package pretimmediat.ext

import android.content.Context
import android.content.res.Resources
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Job
import logger.L
import pretimmediat.R
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.runOnCurrThread
import vector.app.ext.inflate
import vector.ext.inflate
import java.util.concurrent.TimeUnit

private var currToast: Toast? = null
private var cancelJob: Job? = null

fun toast(
    context: Context?,
    vararg texts: String?,
    duration: Int = Toast.LENGTH_SHORT
): Toast? {
    if (texts.isEmpty() || context == null) return null

    var text = String()
    texts.forEach {
        text = text.plus(it)
    }

    if (text.isNotEmpty() && text.isNotBlank()) {
        return showToast(context, text, duration)
    }
    return null
}

fun toast(
    context: Context?,
    @StringRes vararg ids: Int,
    duration: Int = Toast.LENGTH_SHORT
): Toast? {
    if (ids.isEmpty() || context == null) return null

    var text = String()
    try {
        ids.forEach {
            text = text.plus(ContextCompat.getString(context, it))
        }
    } catch (e: Resources.NotFoundException) {
        L.e(e)
        return null
    }

    if (text.isNotEmpty() && text.isNotBlank()) {
        return showToast(context, text, duration)
    }
    return null
}

@Suppress("DEPRECATION")
private fun showToast(context: Context, text: String, duration: Int): Toast {
    cancelJob?.cancel()

    currToast?.cancel()
    currToast = null

    val layout = context.inflate(R.layout.layout_toast).apply {
        findViewById<TextView>(R.id.tv_toast)?.let { tv ->
            tv.text = text
        }
    }
    return Toast(context).apply {
        view = layout
        setDuration(duration)
        show()
        if (isSdkAtLeast(SdkInt.R_30)) {
            addCallback(object : Toast.Callback() {
                override fun onToastHidden() {
                    currToast = null
                }
            })
        } else {
            cancelJob = runOnCurrThread(3, TimeUnit.SECONDS) {
                // 3秒后怎么都消失了
                currToast = null
                cancelJob = null
            }
        }
        currToast = this
    }
}