package pretimmediat.activity

import android.content.Intent
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.MutableStateFlow
import pretimmediat.R
import pretimmediat.activity.user.LoginActivity
import pretimmediat.activity.user.LoginActivityCreator
import pretimmediat.bus.withBus
import pretimmediat.databinding.ActivityMainBinding
import pretimmediat.def.Constants
import pretimmediat.dialog.Style1Dialog
import pretimmediat.dialog.Style2Dialog
import pretimmediat.fragment.HomeFrag
import pretimmediat.fragment.MeFrag
import pretimmediat.fragment.OrderFrag
import pretimmediat.manager.AccountManager
import pretimmediat.model.AppInfo
import vector.app.adapter.pager.AdapterPager
import vector.app.adapter.pager.FragPager
import vector.app.adapter.pager.build
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.ext.bind.bindView
import vector.app.ext.view.setOnDebounceClickListener
import vector.ext.isNotNullOrEmpty
import vector.util.intent.IntentAction

@Creator
@AndroidEntryPoint
class MainActivity : SimpleDBActivityEx() {

    companion object {
        const val TAB_HOME = 0
        const val TAB_ORDER = 1
        const val TAB_ME = 2
    }

    val pager = FragPager.build(
        creators = listOf(
            AdapterPager.PagerCreator { HomeFrag() },
            AdapterPager.PagerCreator { OrderFrag() },
            AdapterPager.PagerCreator { MeFrag() }
        )
    )

    private val ivHome by bindView<ImageView>(R.id.main_iv_home)
    private val ivOrder by bindView<ImageView>(R.id.main_iv_order)
    private val ivMe by bindView<ImageView>(R.id.main_iv_me)

    val currIndex = MutableStateFlow(0)

    @Extra(value = true)
    var requiredTabIndex: Int? = null

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityMainBinding.inflate(inflater).apply {
            owner = this@MainActivity
        }
    }

    override fun initializeContentView() {
        ivHome.setOnDebounceClickListener {
            currIndex.value = TAB_HOME
        }

        ivOrder.setOnDebounceClickListener {
            if (AccountManager.token.isNotNullOrEmpty()) {
                // 已经登录
                currIndex.value = TAB_ORDER
            } else {
                // 未登录,跳转至登录页面
                LoginActivityCreator.create(LoginActivity.FROM_ORDER).start(this)
            }
        }

        ivMe.setOnDebounceClickListener {
            if (AccountManager.token.isNotNullOrEmpty()) {
                // 已经登录
                currIndex.value = TAB_ME
            } else {
                // 未登录,跳转至登录页面
                LoginActivityCreator.create(LoginActivity.FROM_ME).start(this)
            }
        }

        when (requiredTabIndex) {
            TAB_HOME -> currIndex.value = TAB_HOME
            TAB_ORDER -> currIndex.value = TAB_ORDER
            TAB_ME -> currIndex.value = TAB_ME
        }

        withBus().onValue<AppInfo>(Constants.Bus.FORCE_UPDATE_DIALOG) { info ->
            Style1Dialog.Builder(this)
                .icon(R.drawable.dialog_ic_loud_speaker)
                .content(info.promptMsg)
                .button(R.string.upgrade_go_now) {
                    // 跳转到应用市场
                    IntentAction.browser().url(info.appDownUrl).launch()
                }
                .dismissOnClick(false)
                .dismissOnTouchOutside(false)
                .build().apply {
                    setCancelable(false)
                    show()
                }
        }

        withBus().onValue<AppInfo>(Constants.Bus.UPDATE_DIALOG) { info ->
            Style2Dialog.Builder(this)
                .icon(R.drawable.dialog_ic_loud_speaker)
                .content(info.promptMsg)
                .buttonLeft(R.string.upgrade_ignore)
                .buttonRight(R.string.upgrade_go_now) {
                    // 跳转到应用市场
                    IntentAction.browser().url(info.appDownUrl).launch()
                }
                .build()
                .show()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        when (requiredTabIndex) {
            TAB_HOME -> currIndex.value = TAB_HOME
            TAB_ORDER -> currIndex.value = TAB_ORDER
            TAB_ME -> currIndex.value = TAB_ME
        }
        requiredTabIndex = null
    }
}