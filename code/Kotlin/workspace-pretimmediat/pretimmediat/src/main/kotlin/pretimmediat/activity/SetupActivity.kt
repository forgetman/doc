package pretimmediat.activity

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import coroutine.flow.launchIn
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import logger.L
import pretimmediat.R
import pretimmediat.databinding.ActivitySetupBinding
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.withLoading
import pretimmediat.manager.AccountManager
import pretimmediat.property.Properties
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.bindingadapter.bind.Bind
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 设置界面
 */
class SetupActivity : SimpleDBActivityEx() {

    companion object {
        private const val LOG_TAG = "SetupActivity"
    }

    val onFrenchClick = Bind.OnClick {
        lifecycleScope.launch {
            Properties.language.put("fr")
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("fr"))
        finish()
    }

    val onChineseClick = Bind.OnClick {
        lifecycleScope.launch {
            Properties.language.put("en")
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        finish()
    }

    val onLogoutClick = Bind.OnClick {
        callbackFlow {
            val result = suspendCancellableCoroutine { cont ->
                AccountManager.clear {
                    if (it) cont.resume(Unit) else cont.resumeWithException(Exception("Failed to clear account info"))
                }
            }
            send(result)
            close()
        }.withLoading(this).onEach {
            MainActivityCreator.create().requiredTabIndex(MainActivity.TAB_HOME).start(this)
        }.catch { e ->
            L.e(LOG_TAG, e)
        }.launchIn(this)
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivitySetupBinding.inflate(inflater).apply {
            owner = this@SetupActivity
        }
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(R.string.language_title) {
            finish()
        }
    }
}