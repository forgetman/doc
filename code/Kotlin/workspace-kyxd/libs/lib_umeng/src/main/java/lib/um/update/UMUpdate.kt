package lib.um.update

import android.content.Context
import android.widget.Toast

import com.umeng.update.UmengUpdateAgent
import com.umeng.update.UpdateStatus

/**
 * @author yuansui
 */
object UMUpdate {
    /**
     * 检查更新
     *
     * @param context
     * @param silent  是否使用静默模式, false为提示, true为只有新版本的时候才提示
     */
    fun checkUpdate(context: Context, silent: Boolean) {
        UmengUpdateAgent.setUpdateAutoPopup(false)
        UmengUpdateAgent.setUpdateListener { updateStatus, updateInfo ->
            when (updateStatus) {
                UpdateStatus.Yes -> {
                    // has update
                    UmengUpdateAgent.showUpdateDialog(context, updateInfo)
                }
                UpdateStatus.No -> {
                    // has no update
                    if (!silent) {
                        Toast.makeText(context, "没有更新", Toast.LENGTH_SHORT).show()
                    }
                }
                UpdateStatus.NoneWifi -> {
                    // none wifi
                    if (!silent) {
                        Toast.makeText(context, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show()
                    }
                }
                UpdateStatus.Timeout -> {
                    // time out
                    if (!silent) {
                        Toast.makeText(context, "超时", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        UmengUpdateAgent.update(context)
    }
}
