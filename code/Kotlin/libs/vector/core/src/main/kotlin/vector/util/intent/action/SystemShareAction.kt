package vector.util.intent.action

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import logger.L
import vector.MimeType
import vector.util.Launcher
import vector.util.intent.IntentAction
import java.io.File

val IntentAction.Companion.systemShare: SystemShareAction
    get() = SystemShareAction()

@Suppress("EXPECT__CLASSIFIERS_ARE_IN_BETA_WARNING")
class SystemShareAction internal constructor() {

    companion object {
        private const val LOG_TAG = "SystemShareAction"
    }

    fun shareText(context: Context, text: String) {
        val intent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = MimeType.Text.Txt.media
            putExtra(Intent.EXTRA_TEXT, text)
        }, "")
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            L.e(LOG_TAG, e)
        }
    }

    fun shareImage(context: Context, title: String, path: String) {
        val file = File(path)
        val intent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            try {
                val uri = file.toUri()
                putExtra(Intent.EXTRA_STREAM, uri)
            } catch (e: Exception) {
                L.e(LOG_TAG, e)
            }
        }, title)

        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e(LOG_TAG, e.message)
        }
    }

    fun shareUrl(context: Context, title: String, url: String) {
        shareText(context, "$title $url")
    }
}

