package pretimmediat.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pretimmediat.BuildConfig
import pretimmediat.R
import pretimmediat.activity.test.TestActivity
import pretimmediat.dialog.PermissionDialog
import pretimmediat.manager.AccountManager
import pretimmediat.property.Properties
import pretimmediat.stats.Stats
import sugar.ext.runOnMainThread
import vector.app.activity.SimpleActivityEx
import vector.app.util.inflate
import vector.datastore.preference.sync
import vector.ext.startActivity
import java.util.concurrent.TimeUnit

/**
 * 启动页
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : SimpleActivityEx() {

    override fun createContentView(): View {
        return R.layout.activity_splash.inflate(this)
    }

    override fun initializeContentView() {
        // FIXME: resources.properties里配置的默认语言没生效, 在这里手动切换一下, 后续有空再研究
        val locales = AppCompatDelegate.getApplicationLocales()
        val language = Properties.language.sync().getOrNull()
        val size = locales.size()
        var find = false
        for (i in 0 until size) {
            if (locales.get(i)?.language == language) {
                find = true
                break
            }
        }
        if (!find) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
        }
        super.initializeContentView()

        if (AccountManager.isLoggedIn()) {
            FirebaseCrashlytics.getInstance()
                .setUserId("${AccountManager.account},${Properties.accountPhoneNumber},${Properties.gaid}")
            Stats.risk.onEvent("ANNOUNCE_POOR_STAMP")
        } else {
            FirebaseCrashlytics.getInstance().setUserId(Properties.gaid.sync().get())
        }

        if (find) {
            runOnMainThread(1000, TimeUnit.MILLISECONDS, this) {
                if (!isFinishing) {
                    Properties.showPermission.asFirstFlow().filterNotNull().onEach { show ->
                        if (show) {
                            PermissionDialog(this@SplashActivity) { result ->
                                if (result) {
                                    lifecycleScope.launch {
                                        Properties.showPermission.put(false)
                                    }
                                    if (BuildConfig.TEST) {
                                        startActivity<TestActivity>()
                                    } else {
                                        startActivity<MainActivity>()
                                    }
                                    finish()
                                } else {
                                    finish()
                                }
                            }.apply {
                                setOnCancelListener {
                                    finish()
                                }
                            }.show()
                        } else {
                            if (BuildConfig.TEST) {
                                startActivity<TestActivity>()
                            } else {
                                startActivity<MainActivity>()
                            }
                            finish()
                        }

                    }.launchIn(this)
                }
            }
        }
    }
}