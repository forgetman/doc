package dsb.design.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import dsb.App
import dsb.Bus
import dsb.EventId
import dsb.databinding.ActivityMainBinding
import dsb.design.ui.frag.HomeFrag
import dsb.design.ui.frag.MeFrag
import dsb.design.ui.frag.MsgFrag
import dsb.design.ui.frag.ServiceFrag
import dsb.ext.checkSignIn
import dsb.model.GpsCity
import dsb.model.Push
import dsb.network.api.CommonApi
import dsb.serv.LocationServ
import dsb.util.NetUtil
import eson.Eson
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.catch
import lib.base.design.ui.activity.BaseSimpleDBActivity
import lib.base.model.User
import lib.base.network.createApi
import lib.udesk.UDesk
import live.Live
import sugar.ext.coroutines.launch
import vector.app.adapter.pager.fragPagerListOf
import vector.bindingadapter.bind.Bind
import vector.ext.startServ
import vector.ext.stopService
import vector.ext.toast
import vector.util.DangerousPerm
import vector.util.EasyPermissions
import vector.util.intent.IntentAction

/**
 * @author yuansui
 * @since 2019/1/17
 */
@Creator
@AndroidEntryPoint
class MainActivity : BaseSimpleDBActivity() {

    companion object {
        const val TAB_MAIN = 0
        const val TAB_MSG = 1
        const val TAB_SERVICE = 2
        const val TAB_ME = 3

        var running: Boolean = false
    }

    @Extra(true)
    var pushMessage: String? = null

    val unreadCount = Live(0)

    val tabSelected = Live(0)

    val pager = fragPagerListOf {
        add { HomeFrag() }
        add { MsgFrag() }
        add { ServiceFrag() }
        add { MeFrag() }
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityMainBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun flowOfSetup() {
        EasyPermissions.request(this, DangerousPerm.LOCATION {
            when (it) {
                EasyPermissions.Result.GRANT -> startServ<LocationServ>()
                else -> {
                    // do nothing
                    // TODO: 是否需要跳转到设置页面
                }
            }
        })

        Bus.get().with(this).onMessage(EventId.SIGN_IN) {
            val mobile = User.get().mobile
            UDesk.login(mobile, mobile)
        }

        App.unreadCount.observe(this) {
            unreadCount.value = it
        }

        NetUtil.refreshUnreadNumber()

        createApi<CommonApi>()
            .checkVersion()
            .catch {
                // do nothing
            }
            .launch(this) {
                AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(it.title)
                    .setMessage(it.notes)
                    .setPositiveButton("立即更新") { _, _ ->
                        IntentAction.market().launch()
                        finish()
                    }
                    .create()
                    .show()
            }

        onPushReceived()

        running = true
    }

    private fun onPushReceived() {
        if (pushMessage == null) return
        val push = Eson.default().fromJson(pushMessage, Push::class.java) ?: return
        when (push.pageIdx) {
            in 0..100 -> {
                // 消息界面
                if (!checkSignIn()) return
                tabSelected.value = TAB_MSG
                WebViewActivityCreator.create().url(push.url).start(this)
            }
            in 101..199 -> {
                tabSelected.value = TAB_SERVICE
                WebViewActivityCreator.create().url(push.url).start(this)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        onPushReceived()
    }

    val onMainTab = Bind.OnClick {
        tabSelected.value = TAB_MAIN
    }

    val onMsgTab = Bind.OnClick {
        if (!checkSignIn()) return@OnClick
        tabSelected.value = TAB_MSG
    }

    val onInfoTab = Bind.OnClick {
        tabSelected.value = TAB_SERVICE
    }

    val onMeTab = Bind.OnClick {
        tabSelected.value = TAB_ME
    }

    val onChatClick = Bind.OnClick {
        if (!checkSignIn()) return@OnClick
        UDesk.chat(this)
    }

    private var lastExitTime = 0L
    private val enableExit: Boolean
        get() {
            val time = System.currentTimeMillis()
            return if (time - lastExitTime > 2000) {
                lastExitTime = time
                false
            } else {
                true
            }
        }

    override fun onBackPressed() {
        if (enableExit) {
            super.onBackPressed()
        } else {
            toast("再按一次退出")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        GpsCity.clear()
        App.currCity = null
        Bus.close()

        running = false

        App.unreadCount.removeObservers(this)

        stopService<LocationServ>()
    }
}