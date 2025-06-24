package vector.util.intent.action

import android.content.Intent
import android.provider.ContactsContract
import logger.L
import vector.app.delegate.ActivityResultCallback
import vector.util.Launcher
import vector.util.intent.IntentAction

/**
 * 通讯录
 */
val IntentAction.Companion.contacts: ContactsAction
    get() = ContactsAction()

class ContactsAction {

    companion object {
        private const val LOG_TAG = "ContactsAction"
    }

    fun launch(host: Any, callback: ActivityResultCallback) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE)
        }
        try {
            Launcher.registerForActivityResult(host, intent) { resultCode, data ->
                callback.onActivityResult(resultCode, data)
            }
        } catch (e: Exception) {
            L.e(LOG_TAG, e)
        }
    }
}