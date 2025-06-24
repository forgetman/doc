package vector.util.intent.action

import android.content.Intent
import androidx.annotation.StringRes
import logger.L
import sugar.ext.self
import vector.util.Launcher
import vector.util.intent.IntentAction

/**
 * 邮件
 */
val IntentAction.Companion.mail: MailAction
    get() = MailAction()

class MailAction internal constructor() {

    private var address: String? = null
    private var subject: String? = null
    private var subjectId: Int = 0
    private var text: String? = null

    private val intent: Intent
        get() {
            val intent = Intent(Intent.ACTION_SEND)
                .setType("plain/text")
                .putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
                .putExtra(Intent.EXTRA_TEXT, text)
            if (subject.isNullOrEmpty()) {
                intent.putExtra(Intent.EXTRA_SUBJECT, subjectId)
            } else {
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            return intent
        }

    fun address(a: String) = self { address = a }

    fun subject(s: String) = self { subject = s }

    fun subject(@StringRes id: Int) = self { subjectId = id }

    fun text(t: String) = self { text = t }

    fun launch() {
        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e("MailAction", e)
        }
    }
}