package pretimmediat.ext

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import logger.L
import pretimmediat.R
import pretimmediat.dialog.Style2Dialog
import sugar.ext.lifecycle
import vector.util.Launcher

// 断网弹窗要保证同一时间只有一个
private var errorDialogShowing = false

private const val LOG_TAG = "DialogExt"

/**
 * 网络错误弹窗
 */
fun Context?.showErrorDialog() {
    if (this == null) return
    if (errorDialogShowing) return

    // 尝试判断生命周期
    val state = this.lifecycle?.currentState
    errorDialogShowing = if (state != null) {
        if (state.isAtLeast(Lifecycle.State.RESUMED)) {
            // 只有RESUMED状态才弹窗
            L.d(LOG_TAG, "showErrorDialog when state == RESUMED")
            true
        } else {
            // 其他状态不弹窗
            L.d(LOG_TAG, "showErrorDialog ignore")
            return
        }
    } else {
        // context里无法获取生命后期, 无条件弹窗
        L.d(LOG_TAG, "showErrorDialog when state == null")
        true
    }

    Style2Dialog.Builder(this)
        .icon(R.drawable.network_ic_expired)
        .content(R.string.network_error_title)
        .buttonLeft(R.string.withdraw)
        .buttonRight(R.string.open) {
            // 打开WIFI设置页
            Launcher.startActivity(this, Intent(Settings.ACTION_WIFI_SETTINGS))
        }.build().apply {
            setOnDismissListener {
                errorDialogShowing = false
            }
            show()
        }
}

fun Fragment.showErrorDialog() {
    context.showErrorDialog()
}