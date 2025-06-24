package dsb.design.ui.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dsb.App
import dsb.Bus
import dsb.EventId
import dsb.R
import dsb.databinding.ActivitySetupBinding
import dsb.ext.addBackIcon
import dsb.network.URL
import lib.base.Sp
import lib.base.design.ui.activity.BaseSimpleDBActivity
import lib.base.model.User
import live.Live
import vector.bindingadapter.bind.Bind
import vector.ext.bufferString
import vector.ext.setNavigationBarColor
import vector.ext.startActivity
import vector.os.colorRes
import vector.util.PackageUtil

/**
 * @author yuansui
 * @since 2019/1/21
 */
class SetupActivity : BaseSimpleDBActivity() {

    val signOutVisibility = Live(Sp.isSignIn())

    val version = bufferString {
        append(PackageUtil.appName)
        append("app v")
        append(PackageUtil.appVersionName)
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivitySetupBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("设置")
    }

    override fun flowOfSetup() {
        setNavigationBarColor(R.color.app_bg.colorRes)

        Bus.get().with(this).onMessage(EventId.LOGOUT) {
            toOutState()
        }
    }

    val onClick = Bind.OnClick {
        WebViewActivityCreator.create().url(URL.SERVICE).start(this)
    }

    val onLogoutClick = Bind.OnClick {
        startActivity<LogoutActivity>()
    }

    val onSignOutClick = Bind.OnClick {
        toOutState()
        Bus.get().send(EventId.SIGN_OUT)
    }

    /**
     * logout or sign out
     */
    private fun toOutState() {
        signOutVisibility.value = false
        App.unreadCount.value = 0
        User.clear()
    }
}