package vector.util.intent.action

import android.content.Intent
import androidx.core.net.toUri
import logger.L
import vector.util.Launcher
import vector.util.intent.IntentAction
import java.io.File

val IntentAction.Companion.install: InstallAction
    get() = InstallAction()

class InstallAction internal constructor() {

    fun launch(path: String) {
        val apkFile = File(path)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkFile.toUri(), "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e("InstallAction", e)
        }
    }
}